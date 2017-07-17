package FileManager

import junit.framework.Assert.assertEquals
import org.junit.Test
import source.DB.DBConversation
import source.DB.DBManagerK
import source.DB.DBUser
import java.io.BufferedReader
import java.util.*

class Tests {
    val manager: FileInterface = FileManagerK()
    val dbman: DBManagerK = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    @Test
    fun signIn() {
        assertEquals(DBUser("login1", "pass4", "name4", null), manager.authorization("login1", "pass4"))
        assertEquals(DBUser("login2", "pass2", "name2", null), manager.authorization("login2", "pass2"))
        assertEquals(null, manager.authorization("login3", "pass4"))
        assertEquals(null, manager.authorization("", ""))
    }

    @Test
    fun dialogs() {
        assertEquals(emptySet<DBConversation>(), manager.getDialogs(DBUser("login1", "pass4", "name4", null)))

        val user = manager.authorization("login1", "pass4")
        assertEquals(dbman.loadConversations(user!!), manager.getDialogs(user))
        println(manager.getDialogs(user))
    }

    @Test
    fun messages() {
        assertEquals(null, manager.getMessage(DBUser("login1", "pass4", "name4", null), 1, 2))

        val user = manager.authorization("login1", "pass4") ?: return
        manager.getMessage(user, 1, 2)!!.forEach { println(it) }
    }

    @Test
    fun signup() {
        assertEquals(false, manager.signUp("login5", "dd", "name4"))
    }

    @Test
    fun setName() {
        val user = manager.authorization("login1", "pass4") ?: return
        assertEquals(true, manager.setName(user, "New Name"))
        assertEquals(false, manager.setName(manager.authorization("login2", "pass2") ?: return, "New Name"))
    }

    @Test
    fun setPass() {
        val user = manager.authorization("login1", "pass4") ?: return
        assertEquals(true, manager.setPassword(user, "New Pass"))
        assertEquals(true, manager.setPassword(manager.authorization("login2", "pass2") ?: return, "New Name"))
    }

    @Test
    fun createConversation() {
        val user = manager.authorization("login1", "New Pass") ?: return
        manager.createConversation(user, "bar1")
        manager.createConversation(user, "bar2")
    }

    @Test
    fun inviteUser() {
        val user = manager.authorization("login1", "New Pass") ?: return

        manager.inviteUser(user, 6, "login2")
        manager.inviteUser(user, 7, "login2")
    }

}

fun main(array: Array<String>) {
    val manager: FileInterface = FileManagerK()

    val reader = Scanner(System.`in`)
    print("LOGIN: ")
    val login = reader.nextLine()

    print("PASSWORD: ")
    val password = reader.nextLine()

    val user = manager.authorization(login, password) ?: throw IllegalAccessException()

    val dialogs = manager.getDialogs(user)

    dialogs.forEach { println("${it.id}: ${it.name}") }

    print("Choose dialog = ")
    val id_dialog = reader.nextInt()

    val messages = manager.getMessage(user, id_dialog, 20) ?: throw IllegalAccessException()
    messages.forEach { println(it) }

    while (true) {
        manager.sendMessage(user, id_dialog, reader.nextLine())
        println(manager.getMessage(user, id_dialog, 1))
    }
}