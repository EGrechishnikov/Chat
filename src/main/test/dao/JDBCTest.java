package dao;

import jdbc.JDBCConnection;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;

/**
 * @author Evgeniy Grechishnikov
 */
public class JDBCTest {
    @Test
    public void connectionTest() {
        Connection connection = JDBCConnection.getConnection();
        assertNotNull(connection);
        JDBCConnection.closeConnection();
    }
}
