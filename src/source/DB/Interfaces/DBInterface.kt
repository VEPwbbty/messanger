package source.DB.Interfaces

interface DBInterface {
    fun loadUser(login: String): User? //++
    fun User.conversations(): Set<Conversation> //++
    fun Conversation.messages(count: Int): List<Message> //++
    fun Conversation.users(): Set<User> //++

    fun User.save(password: String, name: String): Boolean
    fun addConversation(name: String): Conversation? //++
    fun addUser(login: String, password: String, name: String): User? //+
    fun Conversation.addUser(user: User): Boolean //++
    fun Conversation.kickUser(user: User): Boolean //++
    fun Conversation.addMessage(sender: User, text: String): Boolean //+
}