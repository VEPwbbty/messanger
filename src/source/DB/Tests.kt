package source.DB

import junit.framework.Assert.assertEquals
import org.junit.Test
import source.DB.Interfaces.Conversation
import source.DB.Interfaces.DBInterface

class Tests : DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db") {
    @Test
    fun createUser() {
        assertEquals("DBUser(login=log1, password=pass1, name=nam1)", addUser("log1", "pass1", "nam1").toString())
        assertEquals("DBUser(login=log2, password=pass2, name=nam2)", addUser("log2", "pass2", "nam2").toString())
        assertEquals("DBUser(login=log3, password=pass2, name=nam3)", addUser("log3", "pass2", "nam3").toString())
        assertEquals("null", addUser("log1", "pass2", "nam4").toString())
        assertEquals("DBUser(login=, password=, name=)", addUser("", "", "").toString())
    }

    @Test
    fun loadUser() {
        assertEquals("DBUser(login=log1, password=pass1, name=nam1)", loadUser("log1").toString())
        assertEquals("DBUser(login=log2, password=pass2, name=nam2)", loadUser("log2").toString())
        assertEquals("DBUser(login=log3, password=pass2, name=nam3)", loadUser("log3").toString())
        assertEquals("null", loadUser("dsfds").toString())
        assertEquals("DBUser(login=, password=, name=)", loadUser("").toString())
    }

    @Test
    fun addConversations() {
        assertEquals("DBConversation(id=1, name=conv1)", addConversation("conv1").toString())
        assertEquals("DBConversation(id=2, name=conv2)", addConversation("conv2").toString())
        assertEquals("DBConversation(id=3, name=conv1)", addConversation("conv1").toString())
        assertEquals("DBConversation(id=4, name=)", addConversation("").toString())
    }

    @Test
    fun addUserToConversation() {
        val conversation = addConversation("conv2")!!
        assertEquals(true, conversation.addUser(loadUser("log1")!!))
        assertEquals(true, conversation.addUser(loadUser("log2")!!))
        assertEquals(true, conversation.addUser(loadUser("log3")!!))
        assertEquals(false, conversation.addUser(loadUser("log1")!!))
    }

    @Test
    fun kickUserFromConversation() {
        val conversation = addConversation("conv2")!!
        assertEquals(true, conversation.addUser(loadUser("log1")!!))
        assertEquals(true, conversation.addUser(loadUser("log2")!!))
        assertEquals(true, conversation.addUser(loadUser("log3")!!))
        assertEquals(false, conversation.addUser(loadUser("log1")!!))

        assertEquals(true, conversation.kickUser(loadUser("log1")!!))
        assertEquals(true, conversation.kickUser(loadUser("log2")!!))
        assertEquals(true, conversation.kickUser(loadUser("log3")!!))
        assertEquals(true, conversation.kickUser(loadUser("log1")!!))
    }

    @Test
    fun addMessage() {
        val conversation = addConversation("add message")!!
        val user1 = loadUser("log1")!!
        val user2 = loadUser("log2")!!
        val user3 = loadUser("log3")!!

        conversation.addUser(user1)
        conversation.addUser(user2)

        assertEquals(true, conversation.addMessage(user1, "hello1"))
        assertEquals(true, conversation.addMessage(user1, "mister1"))
        assertEquals(true, conversation.addMessage(user2, "hello2"))
        assertEquals(true, conversation.addMessage(user3, "hello3"))
    }

    @Test
    fun conversations() {
        val user1 = loadUser("log1")!!
        val user2 = loadUser("log2")!!
        val user3 = loadUser("log3")!!
        val user4 = loadUser("log4")!!


        assertEquals(3, user1.conversations().size)
        assertEquals(3, user2.conversations().size)
        assertEquals(2, user3.conversations().size)
        assertEquals(0, user4.conversations().size)

        println(user1.conversations().joinToString(separator = "\n"))
    }

    @Test
    fun messages() {
        val user1 = loadUser("log1")!!
        val user2 = loadUser("log2")!!

        val conversation = addConversation("messages")!!


        conversation.addMessage(user1, "hello1")
        conversation.addMessage(user1, "mister1")
        conversation.addMessage(user2, "hello2")
        conversation.addMessage(user2, "hello3")

        assertEquals(4, conversation.messages(10).size)
        assertEquals(4, conversation.messages(4).size)
        assertEquals(3, conversation.messages(3).size)
        assertEquals(0, conversation.messages(0).size)
        assertEquals(0, conversation.messages(-5).size)

        println(conversation.messages(10).joinToString(separator = "\n"))
    }

    @Test
    fun users() {
        val user1 = loadUser("log1")!!
        val user2 = loadUser("log2")!!

        val conversation = addConversation("users")!!


        conversation.addUser(user1)
        conversation.addUser(user2)

        assertEquals(2, conversation.users().size)

        println(conversation.users().joinToString(separator = "\n"))
    }

    @Test
    fun save() {
        val user1 = loadUser("log1")!!
        user1.save("lala", "la1")
        assertEquals("DBUser(login=log1, password=lala, name=la1)", user1.toString())
    }
}