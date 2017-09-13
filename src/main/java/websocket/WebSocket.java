package websocket;

import bean.Message;
import com.google.gson.Gson;
import dao.MessageDAO;
import org.apache.log4j.Logger;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket for transfer message beans between server and
 * client side.
 */
@ServerEndpoint("/connect")
public class WebSocket {
    private static Logger logger = Logger.getLogger(WebSocket.class);
    private final static String LOAD_COMMAND = "***LOAD#MESSAGES***";
    private final static String STOP_COMMAND = "***STOP#CHAT***";
    //Id последнего полученного сообщения
    private int lastMessageId;
    private static MessageDAO dao = new MessageDAO();
    //Ids моих сообщений
    private List<Integer> myMessagesId = new CopyOnWriteArrayList<Integer>();
    private static Gson gson = new Gson();
    private volatile boolean chatStopped = false;

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message.equals(LOAD_COMMAND)) {
                getAllMessages(session);
                startReadAndPassMessages(session);
            } else if(message.equals(STOP_COMMAND)) {
                chatStopped = true;
            } else {
                int id = dao.save(gson.fromJson(message, Message.class));
                myMessagesId.add(id);
            }
        } catch (Exception e) {
            logger.error("WebSocket Exception: on message error");
            logger.error(e);
        }
    }

    private void getAllMessages(Session session) throws Exception {
        logger.info("get all messages");
        List<Message> list = dao.getAllWithoutMyMessages(myMessagesId, lastMessageId);
        for (Message message : list) {
            session.getBasicRemote().sendText(gson.toJson(message));
            lastMessageId = message.getId();
        }
    }

    private void startReadAndPassMessages(final Session session) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    logger.info("WebSocket: start read messages");
                    while (!chatStopped) {
                        List<Message> list = dao.getAllWithoutMyMessages(myMessagesId, lastMessageId);
                        for (Message message : list) {
                            session.getBasicRemote().sendText(gson.toJson(message));
                            lastMessageId = message.getId();
                        }
                        Thread.sleep(4000);
                    }
                } catch (Exception e) {
                    logger.error("WebSocket Exception: start read messages error");
                    logger.error(e);
                } finally {
                    logger.info("WebSocket: stop read messages");
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}