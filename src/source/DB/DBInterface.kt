package source.DB

interface DBInterface {
    fun loadUser(login: String): DBUser? //++
    fun loadConversations(user: DBUser): Set<DBConversation> //++
    fun loadMessages(conversation: DBConversation, count: Int): List<DBMessage> //++

    fun saveUser(user: DBUser): Boolean //++
    fun addConversation(name: String): DBConversation? //++
    fun addUserToConversation(user: DBUser, conversation: DBConversation): Boolean //++
    fun kickFromConversation(user: DBUser, conversation: DBConversation): Boolean //++
    fun addMessage(sender: DBUser, conversation: DBConversation, text: String): Boolean //++
}