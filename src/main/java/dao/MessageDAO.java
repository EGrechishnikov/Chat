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
    private Connection connection = connection = JDBCConnection.getConnection();

    public int save(String message) {
        int id = 0;
        try {
            String sql = "insert into message (text) values (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, message);
            statement.execute();
            //TODO дописать реализацию. Вернуть id сообщения. Использовать getLastId() для сравнения
        } catch (SQLException e) {
            logger.error("save error");
            logger.error(e);
        }
        return id;
    }

    public List<Message> getAllWithoutMyMessages(List<Integer> myMessagesId) {
        List<Message> messages = new ArrayList<Message>();
        try {
            String sql = "select * from message";
            ResultSet set = connection.prepareStatement(sql).executeQuery();
            while(set.next()) {
                messages.add(new Message(set.getInt(1),
                        set.getString(2)));
            }
        } catch (SQLException e) {
            logger.error("get all error");
            logger.error(e);
        }
        return messages;
    }

    public int getLastId() {
        int lastId = 0;
        try {
            String sql = "select max(id) from message";
            ResultSet set = connection.prepareStatement(sql).executeQuery();
            set.next();
            lastId = set.getInt(1);
        } catch (SQLException e) {
            logger.error("get last id error");
            logger.error(e);
        }
        return lastId;
    }
}
