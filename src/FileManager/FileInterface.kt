package FileManager

import source.DB.Interfaces.Conversation
import source.DB.Interfaces.Message
import source.DB.Interfaces.User

interface FileInterface {
    fun authorization(login: String, password: String): User? //++
    fun getDialogs(user: User): Set<Conversation> //++
    fun getMessage(user: User, id_dialog: Int, count: Int): List<Message>? //++

    fun signUp(login: String, password: String, name: String): Boolean //++
    fun setName(user: User, newName: String): Boolean //++
    fun setPassword(user: User, newPassword: String): Boolean //++
    fun createConversation(user: User, name: String): Conversation? //++
    fun inviteUser(user: User, id_dialog: Int, loginInvited: String): Boolean //+
    fun kickUser(user: User, id_dialog: Int, loginKicked: String): Boolean

    fun sendMessage(user: User, id_dialog: Int, text: String): Set<User>?

    fun exit(user: User)
}