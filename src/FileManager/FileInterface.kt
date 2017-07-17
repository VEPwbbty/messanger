package FileManager

import source.DB.DBConversation
import source.DB.DBMessage
import source.DB.DBUser

interface FileInterface {
    fun authorization(login: String, password: String): DBUser? //++
    fun getDialogs(user: DBUser): Set<DBConversation> //++
    fun getMessage(user: DBUser, id_dialog: Int, count: Int): List<DBMessage>? //++

    fun signUp(login: String, password: String, name: String): Boolean //++
    fun setName(user: DBUser, newName: String): Boolean //++
    fun setPassword(user: DBUser, newPassword: String): Boolean //++
    fun createConversation(user: DBUser, name: String): DBConversation? //++
    fun inviteUser(user: DBUser, id_dialog: Int, loginInvited: String): Boolean //+
    fun kickUser(user: DBUser, id_dialog: Int, loginKicked: String): Boolean

    fun sendMessage(user: DBUser, id_dialog: Int, text: String): Set<DBUser>?

    fun exit(user: DBUser)
}