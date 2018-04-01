package jdbc;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author Evgeniy Grechishnikov
 */
public class JDBCConnection {
    private static Connection connection;
    private static ResourceBundle config = ResourceBundle.getBundle("config");
    private static final String url = config.getString("db.url");
    private static final String login = config.getString("db.user");
    private static final String password = config.getString("db.password");
    private static Logger logger = Logger.getLogger(JDBCConnection.class);

    public static Connection getConnection() {
        try {
            if (connection == null) {
                Class.forName("com.mysql.jdbc.Driver");
                logger.info("Create connection");
                connection = DriverManager.getConnection(url, login, password);
            }
        } catch (Exception e) {
            logger.error("Connection error", e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            getConnection().close();
            logger.info("DB connection closed");
        } catch (SQLException e) {
            logger.error("Connection closeConnection exception", e);
        }
    }
}