package source.DB

import junit.framework.Assert.assertEquals
import org.junit.Test

class Tests {
    val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    @Test
    fun saveUser() {
        assertEquals(true, manager.saveUser(DBUser("login1", "pass1", "name1", null)))
        assertEquals(true, manager.saveUser(DBUser("login2", "pass2", "name2", null)))
        assertEquals(true, manager.saveUser(DBUser("login3", "pass3", "name3", null)))
        assertEquals(true, manager.saveUser(DBUser("login1", "pass4", "name4", null)))
        assertEquals(false, manager.saveUser(DBUser("login5", "pass5", "name3", null)))
    }

    @Test
    fun loadUser() {
        assertEquals(DBUser("login1", "pass4", "name4", null), manager.loadUser("login1"))
        assertEquals(DBUser("login2", "pass2", "name2", null), manager.loadUser("login2"))
        assertEquals(DBUser("login3", "pass3", "name3", null), manager.loadUser("login3"))
        assertEquals(null, manager.loadUser("login4"))
        assertEquals(null, manager.loadUser(""))
    }

    @Test
    fun addConversations() {
        assertEquals(DBConversation(1, "conv1"), manager.addConversation("conv1"))
        assertEquals(DBConversation(2, "conv2"), manager.addConversation("conv2"))
        assertEquals(DBConversation(3, "conv1"), manager.addConversation("conv1"))
        assertEquals(DBConversation(4, ""), manager.addConversation(""))
        assertEquals(DBConversation(5, "c3"), manager.addConversation("c3"))
    }

    @Test
    fun addToConversation() {
        assertEquals(true, manager.addUserToConversation(manager.loadUser("login1")!!, DBConversation(1, "conv1")))
        assertEquals(true, manager.addUserToConversation(manager.loadUser("login2")!!, DBConversation(1, "conv1")))
        assertEquals(false, manager.addUserToConversation(manager.loadUser("login2")!!, DBConversation(1, "conv1")))
        assertEquals(true, manager.addUserToConversation(manager.loadUser("login1")!!, DBConversation(2, "conv2")))
    }

    @Test
    fun loadConversation() {
        val set1 = manager.loadConversations(manager.loadUser("login1")!!)
        val set2 = manager.loadConversations(manager.loadUser("login2")!!)
        val set3 = manager.loadConversations(manager.loadUser("login3")!!)
        println(set1)
        println(set2)
        println(set3)
        assertEquals(2, set1.size)
        assertEquals(1, set2.size)
        assertEquals(0, set3.size)
    }

    @Test
    fun addMessage() {
        val user1 = manager.loadUser("login1")!!
        val user2 = manager.loadUser("login2")!!
        val user3 = manager.loadUser("login3")!!

        val conversation = manager.loadConversations(manager.loadUser("login2")!!).iterator().next()

        assertEquals(true, manager.addMessage(user1, conversation, "Hello world by user1"))
        assertEquals(true, manager.addMessage(user2, conversation, "Hello world by user2"))
        assertEquals(true, manager.addMessage(user3, conversation, "Hello world by user3"))
        assertEquals(true, manager.addMessage(user1, DBConversation(4, ""), "Fourth dialog"))
        assertEquals(false, manager.addMessage(user3, DBConversation(6, ""), "Nope"))
    }

    @Test
    fun loadMessages() {
        assertEquals(3, manager.loadMessages(DBConversation(1, "conv1"), 3).size)
        assertEquals(0, manager.loadMessages(DBConversation(6, "conv1"), 1).size)
        assertEquals(3, manager.loadMessages(DBConversation(1, "conv1"), 100).size)
        assertEquals(0, manager.loadMessages(DBConversation(1, "conv1"), 0).size)
        assertEquals(0, manager.loadMessages(DBConversation(1, "conv1"), -1).size)
    }

    @Test
    fun kick() {
        val user1 = manager.loadUser("login1")!!
        val user2 = manager.loadUser("login2")!!
        val user3 = manager.loadUser("login3")!!

        val conversation = manager.loadConversations(manager.loadUser("login2")!!).iterator().next()

        assertEquals(true, manager.kickFromConversation(user1, conversation))
        assertEquals(true, manager.kickFromConversation(user2, conversation))
        assertEquals(true, manager.kickFromConversation(user3, conversation))
    }
}