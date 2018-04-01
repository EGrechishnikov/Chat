package websocket;

import bean.Message;
import com.google.gson.Gson;
import dao.MessageDAO;
import jdbc.JDBCConnection;
import org.apache.log4j.Logger;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket for transfer message beans between server and
 * client side.
 *
 * @author Evgeniy Grechishnikov
 */
@SuppressWarnings("unused")
@ServerEndpoint("/connect")
public class WebSocket {
    private Logger logger = Logger.getLogger(WebSocket.class);
    private static ResourceBundle config = ResourceBundle.getBundle("config");
    private static final int MESSAGE_READING_TIMEOUT =
            Integer.parseInt(config.getString("message.reading.timeout"));
    private static final String START_COMMAND = config.getString("command.start");
    private static final String STOP_COMMAND = config.getString("command.end");
    private int lastMessageId;
    private List<Integer> myMessagesId = new CopyOnWriteArrayList<>();
    private static MessageDAO dao = new MessageDAO();
    //Online users count
    private static volatile AtomicInteger currentOnlineUsersCount = new AtomicInteger(0);
    //Flag to stop chat
    private volatile boolean chatStopped = false;
    private Gson gson = new Gson();

    /**
     * Message handler. The chat starts here if message contains start command.
     * Also the chat stops here if message contains stop command.
     *
     * @param message - message from client
     * @param session - current session (for answer)
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message.equals(START_COMMAND)) {
                getAllMessages(session);
                startReadAndPassMessages(session);
                currentOnlineUsersCount.incrementAndGet();
                logger.info("Current users count: " + currentOnlineUsersCount.get());
            } else if (message.equals(STOP_COMMAND)) {
                chatStopped = true;
            } else {
                int id = dao.save(gson.fromJson(message, Message.class));
                myMessagesId.add(id);
            }
        } catch (Exception e) {
            logger.error("WebSocket Exception: on message error", e);
        }
    }

    /**
     * Load all messages on start chat
     *
     * @param session - session for sending messages
     * @throws IOException - answer sending exception
     */
    private void getAllMessages(Session session) throws IOException {
        logger.info("get all messages");
        List<Message> list = dao.getAllWithoutMyMessages(myMessagesId, lastMessageId);
        for (Message message : list) {
            session.getBasicRemote().sendText(gson.toJson(message));
            lastMessageId = message.getId();
        }
    }

    /**
     * Automatically message reading from data base.
     *
     * @param session - session for sending messages
     */
    private void startReadAndPassMessages(final Session session) {
        Thread thread = new Thread(() -> {
            try {
                logger.info("WebSocket: start read messages");
                while (!chatStopped) {
                    List<Message> list = dao.getAllWithoutMyMessages(myMessagesId, lastMessageId);
                    for (Message message : list) {
                        session.getBasicRemote().sendText(gson.toJson(message));
                        lastMessageId = message.getId();
                    }
                    session.getBasicRemote().sendText("Users count:" + currentOnlineUsersCount.get());
                    Thread.sleep(MESSAGE_READING_TIMEOUT);
                }
            } catch (Exception e) {
                logger.error("WebSocket Exception: start read messages error", e);
            } finally {
                JDBCConnection.closeConnection();
                currentOnlineUsersCount.decrementAndGet();
                logger.info("WebSocket: stop read messages");
                logger.info("Current users count: " + currentOnlineUsersCount.get());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}