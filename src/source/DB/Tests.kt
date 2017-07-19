package source.DB

import junit.framework.Assert.assertEquals
import org.junit.Test
import source.DB.Interfaces.Conversation
import source.DB.Interfaces.DBInterface

class Tests {
    val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    @Test
    fun createUser() {
        assertEquals("DBUser(login=log1, password=pass1, name=nam1)", manager.addUser("log1", "pass1", "nam1").toString())
        assertEquals("DBUser(login=log2, password=pass2, name=nam2)", manager.addUser("log2", "pass2", "nam2").toString())
        assertEquals("DBUser(login=log3, password=pass2, name=nam3)", manager.addUser("log3", "pass2", "nam3").toString())
        assertEquals("null", manager.addUser("log1", "pass2", "nam4").toString())
        assertEquals("DBUser(login=, password=, name=)", manager.addUser("", "", "").toString())
    }

    @Test
    fun loadUser() {
        assertEquals("DBUser(login=log1, password=pass1, name=nam1)", manager.loadUser("log1").toString())
        assertEquals("DBUser(login=log2, password=pass2, name=nam2)", manager.loadUser("log2").toString())
        assertEquals("DBUser(login=log3, password=pass2, name=nam3)", manager.loadUser("log3").toString())
        assertEquals("null", manager.loadUser("dsfds").toString())
        assertEquals("DBUser(login=, password=, name=)", manager.loadUser("").toString())
    }

    @Test
    fun addConversations() {
        assertEquals("DBConversation(id=1, name=conv1)", manager.addConversation("conv1").toString())
        assertEquals("DBConversation(id=2, name=conv2)", manager.addConversation("conv2").toString())
        assertEquals("DBConversation(id=3, name=conv1)", manager.addConversation("conv1").toString())
        assertEquals("DBConversation(id=4, name=)", manager.addConversation("").toString())
    }

    @Test
    fun addUserToConversation() {
        val conversation = manager.addConversation("conv2")!!
        assertEquals(true, manager.run { conversation.addUser(loadUser("log1")!!) })
        assertEquals(true, manager.run { conversation.addUser(loadUser("log2")!!) })
        assertEquals(true, manager.run { conversation.addUser(loadUser("log3")!!) })
        assertEquals(false, manager.run { conversation.addUser(loadUser("log1")!!) })
    }

    @Test
    fun kickUserFromConversation() {
        val conversation = manager.addConversation("conv2")!!
        assertEquals(true, manager.run { conversation.addUser(loadUser("log1")!!) })
        assertEquals(true, manager.run { conversation.addUser(loadUser("log2")!!) })
        assertEquals(true, manager.run { conversation.addUser(loadUser("log3")!!) })
        assertEquals(false, manager.run { conversation.addUser(loadUser("log1")!!) })

        assertEquals(true, manager.run { conversation.kickUser(loadUser("log1")!!) })
        assertEquals(true, manager.run { conversation.kickUser(loadUser("log2")!!) })
        assertEquals(true, manager.run { conversation.kickUser(loadUser("log3")!!) })
        assertEquals(true, manager.run { conversation.kickUser(loadUser("log1")!!) })
    }

    @Test
    fun addMessage() {
        val conversation = manager.addConversation("add message")!!
        val user1 = manager.loadUser("log1")!!
        val user2 = manager.loadUser("log2")!!
        val user3 = manager.loadUser("log3")!!

        manager.run { conversation.addUser(user1) }
        manager.run { conversation.addUser(user2) }

        assertEquals(true, manager.run { conversation.addMessage(user1, "hello1") })
        assertEquals(true, manager.run { conversation.addMessage(user1, "mister1") })
        assertEquals(true, manager.run { conversation.addMessage(user2, "hello2") })
        assertEquals(true, manager.run { conversation.addMessage(user3, "hello3") })
    }

    @Test
    fun conversations() {
        val user1 = manager.loadUser("log1")!!
        val user2 = manager.loadUser("log2")!!
        val user3 = manager.loadUser("log3")!!
        val user4 = manager.loadUser("log4")!!


        assertEquals(3, manager.run{ user1.conversations().size })
        assertEquals(3, manager.run{ user2.conversations().size })
        assertEquals(2, manager.run{ user3.conversations().size })
        assertEquals(0, manager.run{ user4.conversations().size })

        println(manager.run{ user1.conversations() }.joinToString(separator = "\n"))
    }

    @Test
    fun messages() {
        val user1 = manager.loadUser("log1")!!
        val user2 = manager.loadUser("log2")!!

        val conversation = manager.addConversation("messages")!!


        manager.run { conversation.addMessage(user1, "hello1") }
        manager.run { conversation.addMessage(user1, "mister1") }
        manager.run { conversation.addMessage(user2, "hello2") }
        manager.run { conversation.addMessage(user2, "hello3") }

        assertEquals(4, manager.run{ conversation.messages(10).size })
        assertEquals(4, manager.run{ conversation.messages(4).size })
        assertEquals(3, manager.run{ conversation.messages(3).size })
        assertEquals(0, manager.run{ conversation.messages(0).size })
        assertEquals(0, manager.run{ conversation.messages(-5).size })

        println(manager.run{ conversation.messages(10) }.joinToString(separator = "\n"))
    }

    @Test
    fun users() {
        val user1 = manager.loadUser("log1")!!
        val user2 = manager.loadUser("log2")!!

        val conversation = manager.addConversation("users")!!


        manager.run { conversation.addUser(user1) }
        manager.run { conversation.addUser(user2) }

        assertEquals(2, manager.run{ conversation.users().size })

        println(manager.run{ conversation.users() }.joinToString(separator = "\n"))
    }

    @Test
    fun save() {
        val user1 = manager.loadUser("log1")!!
        manager.run { user1.save("lala", "la1") }
        assertEquals("DBUser(login=log1, password=lala, name=la1)", user1.toString())
    }
}