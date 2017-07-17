package source.DB

import org.sqlite.JDBC
import java.sql.*
import java.text.SimpleDateFormat

class DBManagerK(way: String) : DBInterface {
    /**
     * Constant, that keep address of connection
     */
    private val CON_STR = "jdbc:sqlite:"
    /**
     * Used to do sql command
     */
    private val statement: Statement

    init {
        DriverManager.registerDriver(JDBC())
        this.statement = DriverManager.getConnection(CON_STR + way).createStatement()
        this.statement.execute("PRAGMA foreign_keys = ON;")
    }

    /**
     * Load from DB user which has this login
     * @param login of user
     */
    override fun loadUser(login: String): DBUser? {
        try {
            val user = statement.executeQuery(
                    "SELECT NAME, PASSWORD, LASTVISIT " +
                            "FROM USER " +
                            "WHERE LOGIN = '$login';")
            val name = user.getString("NAME")
            val password = user.getString("PASSWORD")
            val lastVisit = Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.getString("LASTVISIT")).time)
            return DBUser(name = name, login = login, password = password, lastVisit = lastVisit)
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Load all conversations where exist the user
     */
    override fun loadConversations(user: DBUser): Set<DBConversation> {
        try {
            //Get conversations where exist the user
            val conversations = statement.executeQuery("SELECT DISTINCT c.ID, c.NAME " +
                    "FROM (CLIENT_CONV cc INNER JOIN CONVERSATION c ON (cc.ID_CONV = c.ID)) " +
                    "WHERE cc.LOGIN_CLIENT = '${user.login}';")

            val resultSet = mutableSetOf<DBConversation>()

            while (conversations.next()) {
                val id = conversations.getInt("ID")
                val name = conversations.getString("NAME")

                resultSet.add(DBConversation(id = id, name = name))
            }
            return resultSet
        } catch (e: SQLException) {
            e.printStackTrace()
            return emptySet()
        }
    }

    /**
     * Load all users the conversation
     */
    override fun loadUsers(conversation: DBConversation): Set<DBUser> {
        try {
            //Get users
            val users = statement.executeQuery("SELECT LOGIN_CLIENT FROM CLIENT_CONV WHERE ID_CONV = '${conversation.id}';")

            val middleSet = mutableSetOf<String>()
            while (users.next()) {
                middleSet.add(users.getString("LOGIN_CLIENT"))
            }
            return middleSet.map { loadUser(it)!! }.toSet()
        } catch (e: SQLException) {
            e.printStackTrace()
            return emptySet()
        }
    }

    /**
     * Load last count messages from conversation
     */
    override fun loadMessages(conversation: DBConversation, count: Int): List<DBMessage> {
        try {
            val messages = statement.executeQuery("SELECT LOGIN_CLIENT, MESSAGE, TIME " +
                    "FROM MESSAGE " +
                    "WHERE ID_CONV = '${conversation.id}' " +
                    "ORDER BY ID DESC;")

            val result = mutableListOf<DBMessage>()

            for (i in 0 until count) {
                if (!messages.next()) break
                val login = messages.getString("LOGIN_CLIENT")
                val message = messages.getString("MESSAGE")
                val time = Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(messages.getString("TIME")).time)
                result.add(0, DBMessage(login_user = login, id_conv = conversation.id, text = message, time = time))
            }
            return result
        } catch (e: SQLException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * Kick the user from the conversation
     */
    override fun kickFromConversation(user: DBUser, conversation: DBConversation) = execute("" +
            "DELETE FROM CLIENT_CONV " +
            "WHERE LOGIN_CLIENT = '${user.login}' AND ID_CONV = '${conversation.id}'")

    /**
     * Create new user if it doesn't exist, or update it
     */
    override fun saveUser(user: DBUser): Boolean {
        try {
            //Count of users which have current login (I mean login of the user). If 0, then new user will be created,
            //or the existing user will be updated
            val count = statement.executeQuery("SELECT COUNT(LOGIN) " +
                    "FROM (" +
                    "SELECT LOGIN " +
                    "FROM USER " +
                    "WHERE LOGIN = '${user.login}'" +
                    ");").getInt(1)

            if (count == 0) createUser(user)
            else updateUser(user)

            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Create a new line in DB for the user
     */
    private fun createUser(user: DBUser) {
        statement.execute("INSERT INTO USER (LOGIN, PASSWORD, NAME) " +
                "VALUES ('${user.login}', '${user.password}', '${user.name}');")
    }

    /**
     * Change password, name and last visit of the user in DB
     */
    private fun updateUser(user: DBUser) {
        statement.execute("UPDATE USER " +
                "SET PASSWORD = '${user.password}', " +
                "LASTVISIT = ${if (user.lastVisit != null) "'${user.lastVisit}'" else "CURRENT_TIMESTAMP"}, " +
                "NAME = '${user.name}' " +
                "WHERE LOGIN = '${user.login}';")
    }

    /**
     * Add new conversation in DB
     */
    override fun addConversation(name: String): DBConversation? {
        try {
            var lastID = statement.executeQuery("SELECT MAX(ID) FROM CONVERSATION").getInt(1)
            lastID++
            statement.execute("INSERT INTO CONVERSATION (ID, NAME) " +
                    "VALUES ('$lastID', '$name')")
            return DBConversation(id = lastID, name = name)
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Add the user into the conversation
     */
    override fun addUserToConversation(user: DBUser, conversation: DBConversation) = execute("" +
            "INSERT INTO CLIENT_CONV (LOGIN_CLIENT, ID_CONV) " +
            "VALUES ('${user.login}', '${conversation.id}');")

    /**
     * Add the text of new message of sender into special table
     */
    override fun addMessage(sender: DBUser, conversation: DBConversation, text: String) = execute("" +
            "INSERT INTO MESSAGE (LOGIN_CLIENT, ID_CONV, MESSAGE) " +
            "VALUES ('${sender.login}', '${conversation.id}', '${text.replace("'", "''")}');")

    private fun execute(sql: String): Boolean {
        try {
            statement.execute(sql)
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }
}