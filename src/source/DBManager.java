package source;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
     * Used to do sql command
     */
    private Statement statement;

    /**
     * @param way to database
     * @throws SQLException when the class has problems with connection
     */
    public DBManager(String way) throws SQLException {
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR + way);
        this.statement = this.connection.createStatement();
    }

    /**
     * Get user by his login and password, but there is no this combination, then return null
     *
     * @param login    of user
     * @param password of user
     * @return user or null
     */
    public User getUser(String login, String password) {
        try {
            ResultSet user = statement.executeQuery("SELECT ID, NAME " +
                    "FROM USER " +
                    "WHERE LOGIN = '" + login + "' AND PASSWORD = '" + password + "';");
            return new User(user.getInt("ID"), user.getString("NAME"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add to DB new user
     *
     * @param login    of user
     * @param password of user
     * @param name     of user
     * @return success this operation
     */
    public User addUser(String login, String password, String name) {
        try {
            statement.execute("INSERT INTO USER (LOGIN, PASSWORD, NAME) " +
                    "VALUES ('" + login + "', '" + password + "', '" + name + "');");
            return getUser(login, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all users in DB
     *
     * @return set of users
     */
    public Set<User> getUsers() {
        try {
            ResultSet user = statement.executeQuery("SELECT ID, NAME " +
                    "FROM USER;");

            Set<User> users = new HashSet<>();
            while (user.next())
                users.add(new User(user.getInt("ID"), user.getString("NAME")));
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<User>();
        }
    }

    /**
     * Change user's name
     * @param newName of user
     * @return success
     */
    public boolean changeUserName(User user, String newName) {
        try {
            statement.execute("UPDATE USER " +
                    "SET NAME = '" + newName + "' " +
                    "WHERE ID = '" + user.getId() + "';");
            user.setName(newName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Change user's password
     * @param newPassword of user
     * @return success
     */
    public boolean changeUserPassword(User user, String newPassword) {
        try {
            statement.execute("UPDATE USER " +
                    "SET PASSWORD = '" + newPassword + "' " +
                    "WHERE ID = '" + user.getId() + "';");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
