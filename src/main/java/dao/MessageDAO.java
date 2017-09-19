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

/**
 * DAO for messages
 */
public class MessageDAO {
    private static Logger logger = Logger.getLogger(MessageDAO.class);
    private Connection connection = JDBCConnection.getConnection();

    /**
     * Save the message in database
     * @param message - Message pojo
     * @return - the messages id in database
     */
    public int save(Message message) {
        int id = 0;
        try {
            String sql = "insert into message (text, sender, date) values (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, message.getText());
            statement.setString(2, message.getSender());
            statement.setTimestamp(3, message.getDate());
            statement.execute();
            sql = "select max(id) from message where text = ? and sender = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, message.getText());
            statement.setString(2, message.getSender());
            ResultSet set = statement.executeQuery();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            logger.error("MessageDAO Exception: save error");
            logger.error(e);
        }
        return id;
    }

    /**
     * Get all messages excluded my messages
     * @param myMessagesId - list of my messages id
     * @param lastMessagesId - last showed messages id
     * @return - all another messages
     */
    public List<Message> getAllWithoutMyMessages(List<Integer> myMessagesId, int lastMessagesId) {
        List<Message> messages = new ArrayList<Message>();
        try {
            String sql = "select * from message where id > ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, lastMessagesId);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                if(!myMessagesId.contains(set.getInt(1))) {
                    Message message = new Message(set.getInt(1), set.getString(2),
                            set.getString(3), set.getTimestamp(4));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            logger.error("MessageDAO Exception: get all error");
            logger.error(e);
        }
        return messages;
    }
}