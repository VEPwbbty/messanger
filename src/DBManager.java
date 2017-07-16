import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class allow us to communicate with our database, that stores data of the messenger
 */
public class DBManager {
    /**
     * Constant, that keep address of connection
     */
    private static final String CON_STR = "jdbc:sqlite:";

    /**
     * Store connection to our database
     */
    private Connection connection;

    /**
     * @param way to database
     * @throws SQLException when the class has problems with connection
     */
    DBManager(String way) throws SQLException{
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR + way);
    }

    
}
