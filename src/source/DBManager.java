package source;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

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

    private Map<String, User> users;

    /**
     * @param way to database
     * @throws SQLException when the class has problems with connection
     */
    public DBManager(String way) throws SQLException {
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR + way);
        this.statement = this.connection.createStatement();

        this.users = loadUsers();
    }

    private Map<String, User> loadUsers() {
        try {
            ResultSet user = statement.executeQuery("SELECT ID, NAME " +
                    "FROM USER;");

            Map<String, User> users = new HashMap<>();
            while (user.next())
                users.put(user.getString("LOGIN"),
                        new User(user.getInt("ID"), user.getString("NAME"),
                                user.getString("LOGIN"), user.getString("PASSWORD")));
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private User loadUser(String login) {
        try {
            ResultSet user = statement.executeQuery("SELECT ID, NAME " +
                    "FROM USER " +
                    "WHERE LOGIN = '" + login + "';");
            return new User(user.getInt("ID"), user.getString("NAME"),
                    user.getString("LOGIN"), user.getString("PASSWORD"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get user by his login and password, but there is no this combination, then return null
     *
     * @param login    of user
     * @param password of user
     * @return user or null
     */
    public User getUser(String login, String password) {
        if (!users.containsKey(login)) return null;
        User target = users.get(login);
        if (target.getPassword().equals(password)) return target;
        throw new IllegalArgumentException();
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
            User newUser = loadUser(login);
            users.put(login, newUser);
            return newUser;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all users in DB
     *
     * @return collection of users
     */
    public Collection<User> getUsers() {
        return users.values();
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
            users.get(user.getLogin()).setName(newName);
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
            users.get(user.getLogin()).setPassword(newPassword);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
