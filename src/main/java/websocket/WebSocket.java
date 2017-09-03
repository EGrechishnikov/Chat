package websocket;

import bean.Message;
import dao.MessageDAO;
import org.apache.log4j.Logger;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket for transfer message beans between server and
 * client side.
 */
@ServerEndpoint("/connect")
public class WebSocket {
    private static Logger logger = Logger.getLogger(WebSocket.class);
    private final static String LOAD_COMMAND = "***LOAD#MESSAGES***";
    private int lastMessageId;
    private MessageDAO dao = new MessageDAO();
    private List<Integer> myMessagesId = new ArrayList<Integer>();

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message.equals(LOAD_COMMAND)) {
                getAllMessages(session);
            } else {
                int id = dao.save(message);
                myMessagesId.add(id);
            }
        } catch (Exception e) {
            logger.error("on message error");
            logger.error(e);
        }
    }

    private void getAllMessages(Session session) throws Exception {
        logger.info("get all messages");
        List<Message> list = dao.getAllWithoutMyMessages(myMessagesId);
        for (Message message : list) {
            session.getBasicRemote().sendText(message.getText());
            lastMessageId = message.getId();
        }
        startReadAndPassMessages(session);
    }

    private void startReadAndPassMessages(final Session session) {
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        List<Message> list = dao.getAllWithoutMyMessages(myMessagesId);
                        for(Message message : list) {
                            session.getBasicRemote().sendText(message.getText());
                            lastMessageId = message.getId();
                        }
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    logger.error("start read error");
                    logger.error(e);
                }
            }
        }.start();
    }
}