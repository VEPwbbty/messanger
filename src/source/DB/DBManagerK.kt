package source.DB

import org.sqlite.JDBC
import source.DB.Interfaces.*
import java.sql.*
import java.text.SimpleDateFormat

private data class DBUser(override val login: String,
                          override var password: String,
                          override var name: String) : User

private data class DBConversation(override val id: Int, override val name: String) : Conversation

data class DBMessage(override val login_user: String,
                     override val id_conv: Int,
                     override val text: String,
                     override val time: Timestamp) : Message


open class DBManagerK(way: String) : DBInterface {
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
    protected fun loadUser(login: String): User? {
        try {
            val user = statement.executeQuery(
                    "SELECT NAME, PASSWORD " +
                            "FROM USER " +
                            "WHERE LOGIN = '$login';")
            val name = user.getString("NAME")
            val password = user.getString("PASSWORD")

            return DBUser(name = name, login = login, password = password)
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Load all conversations where exist the user
     */
    override fun User.conversations(): Set<Conversation> {
        try {
            //Get conversations where exist the user
            val conversations = statement.executeQuery("SELECT DISTINCT c.ID, c.NAME " +
                    "FROM (CLIENT_CONV cc INNER JOIN CONVERSATION c ON (cc.ID_CONV = c.ID)) " +
                    "WHERE cc.LOGIN_CLIENT = '$login';")

            val resultSet = mutableSetOf<Conversation>()

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
    override fun Conversation.users(): Set<User> {
        try {
            //Get users
            val users = statement.executeQuery("SELECT LOGIN_CLIENT FROM CLIENT_CONV WHERE ID_CONV = '$id';")

            val middleSet = mutableSetOf<String>()

            while (users.next())
                middleSet.add(users.getString("LOGIN_CLIENT"))

            return middleSet.mapNotNull { loadUser(it) }.toSet()
        } catch (e: SQLException) {
            e.printStackTrace()
            return emptySet()
        }
    }

    /**
     * Load last count messages from conversation
     */
    override fun Conversation.messages(count: Int): List<Message> {
        try {
            val messages = statement.executeQuery("SELECT LOGIN_CLIENT, MESSAGE, TIME " +
                    "FROM MESSAGE " +
                    "WHERE ID_CONV = '$id' " +
                    "ORDER BY ID DESC;")

            val result = mutableListOf<Message>()

            for (i in 0 until count) {
                if (!messages.next()) break
                val login = messages.getString("LOGIN_CLIENT")
                val message = messages.getString("MESSAGE")
                val time = Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(messages.getString("TIME")).time)
                result.add(DBMessage(login_user = login, id_conv = id, text = message, time = time))
            }
            return result.reversed()
        } catch (e: SQLException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * Kick the user from the conversation
     */
    override fun Conversation.kickUser(user: User) = execute("" +
            "DELETE FROM CLIENT_CONV " +
            "WHERE LOGIN_CLIENT = '${user.login}' AND ID_CONV = '$id'")

    /**
     * Update this user
     */
    override fun User.save(password: String, name: String): Boolean {
        if (!execute("UPDATE USER SET PASSWORD = '$password', NAME = '$name' WHERE LOGIN = '$login';")) return false
        this as DBUser
        this.name = name
        this.password = password
        return true
    }


    /**
     * Create a new line in DB for the user
     */
    override fun addUser(login: String, password: String, name: String): User? {
        if (!execute("INSERT INTO USER (LOGIN, PASSWORD, NAME) VALUES ('$login', '$password', '$name');"))
            return null
        return loadUser(login)
    }

    /**
     * Add new conversation in DB
     */
    override fun addConversation(name: String): Conversation? {
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
    override fun Conversation.addUser(user: User) = execute("" +
            "INSERT INTO CLIENT_CONV (LOGIN_CLIENT, ID_CONV) " +
            "VALUES ('${user.login}', '$id');")

    //TODO Check on text
    /**
     * Add the text of new message of sender into special table
     */
    override fun Conversation.addMessage(sender: User, text: String) = execute("" +
            "INSERT INTO MESSAGE (LOGIN_CLIENT, ID_CONV, MESSAGE) " +
            "VALUES ('${sender.login}', '$id', '${text.replace("'", "''")}');")


    private fun execute(sql: String): Boolean =
            try {
                statement.execute(sql)
                true
            } catch (e: SQLException) {
                e.printStackTrace()
                false
            }

}