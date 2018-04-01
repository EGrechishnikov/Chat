package dao;

import bean.Message;
import jdbc.JDBCConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Evgeniy Grechishnikov
 */
public class MessageDAO {
    private static Logger logger = Logger.getLogger(MessageDAO.class);
    private Connection connection = JDBCConnection.getConnection();
    private static ResourceBundle config = ResourceBundle.getBundle("config");
    private static final String INSERT_SQL = config.getString("sql.insert");
    private static final String GET_ID_SQL = config.getString("sql.select.id");
    private static final String GET_ALL_SQL = config.getString("sql.select.all");

    /**
     * Save the message in database
     *
     * @param message - Message to save
     * @return - the messages id in database
     */
    public int save(Message message) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, message.getText());
            statement.setString(2, message.getSender());
            statement.setTimestamp(3, message.getDate());
            statement.execute();
            return getLastMessageId(message);
        } catch (SQLException e) {
            logger.error("MessageDAO Exception: save error", e);
            return -1;
        }
    }

    /**
     * Get last message id
     *
     * @param message - message for search
     * @return - id
     */
    private int getLastMessageId(Message message) {
        try (PreparedStatement statement = connection.prepareStatement(GET_ID_SQL)) {
            statement.setString(1, message.getText());
            statement.setString(2, message.getSender());
            ResultSet set = statement.executeQuery();
            set.next();
            return set.getInt(1);
        } catch (SQLException e) {
            logger.error("MessageDAO Exception: get last id error", e);
            return -1;
        }
    }

    /**
     * Get all messages excluded my messages
     *
     * @param myMessagesId   - list of my messages id
     * @param lastMessagesId - last showed messages id
     * @return - all another messages
     */
    public List<Message> getAllWithoutMyMessages(List<Integer> myMessagesId, int lastMessagesId) {
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_ALL_SQL)) {
            statement.setInt(1, lastMessagesId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                if (!myMessagesId.contains(set.getInt(1))) {
                    Message message = new Message(set.getInt(1), set.getString(2),
                            set.getString(3), set.getTimestamp(4));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            logger.error("MessageDAO Exception: get all error", e);
        }
        return messages;
    }
}