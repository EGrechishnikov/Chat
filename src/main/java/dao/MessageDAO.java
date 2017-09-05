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

    public int save(String message) {
        int id = 0;
        try {
            String sql = "insert into message (text) values (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, message);
            statement.execute();
            sql = "select max(id) from message where text = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, message);
            ResultSet set = statement.executeQuery();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            logger.error("save error");
            logger.error(e);
        }
        return id;
    }

    public List<Message> getAllWithoutMyMessages(List<Integer> myMessagesId, int lastMessagesId) {
        List<Message> messages = new ArrayList<Message>();
        try {
            String sql = "select * from message where id > ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, lastMessagesId);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                if(!myMessagesId.contains(set.getInt(1))) {
                    messages.add(new Message(set.getInt(1),
                            set.getString(2)));
                }
            }
        } catch (SQLException e) {
            logger.error("get all error");
            logger.error(e);
        }
        return messages;
    }
}