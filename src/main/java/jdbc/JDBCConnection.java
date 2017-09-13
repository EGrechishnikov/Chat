package jdbc;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection
 */
public class JDBCConnection {
    private static Connection connection;
    private static final String url = "jdbc:mysql://localhost:3306/chat?useUnicode=true&characterEncoding=utf8";
    private static final String login = "root";
    private static final String password = "";
    private static Logger logger = Logger.getLogger(JDBCConnection.class);

    public static Connection getConnection() {
        try {
            if (connection == null) {
                logger.info("Create connection");
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, login, password);
            }
        } catch (Exception e) {
            logger.error("Connection error");
            logger.error(e);
        }
        return connection;
    }

    public static void close() {
        try {
            getConnection().close();
            logger.info("DB connection closed");
        } catch (SQLException e) {
            logger.error("Close connection exception");
            logger.error(e);
        }
    }
}