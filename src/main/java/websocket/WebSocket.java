package websocket;

import org.apache.log4j.Logger;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/connect")
public class WebSocket {
    private static Logger logger = Logger.getLogger(WebSocket.class);
    private final static String LOAD_COMMAND = "***LOAD#MESSAGES***";

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if(message.equals(LOAD_COMMAND)) {
                logger.info(LOAD_COMMAND);
                getAllMessages(session);
            } else {
                logger.info("SIMMPLE MESSAGE");
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void getAllMessages(Session session) throws Exception {
        logger.info("GET ALL");
        String[] arr = {"1", "2", "3"};
        logger.info("SENDING ARRAY");
        for(int i=0; i<arr.length; i++) {
            session.getBasicRemote().sendText(arr[i]);
        }
        startReadAndPassMessages(session);
    }

    private void startReadAndPassMessages(final Session session) {
        new Thread(){
            public void run() {
                    try {
                        while (true) {
                            session.getBasicRemote().sendText("TEST");
                            logger.info("SEND TMP MESSAGE");
                            Thread.sleep(5000);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    logger.info("END SENDING TMP MESSAGE");
            }
        }.start();
    }
}
