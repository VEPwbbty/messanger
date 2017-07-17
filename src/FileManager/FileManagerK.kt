package FileManager

import source.DB.*
import sun.plugin2.message.Conversation

class FileManagerK : FileInterface {

    private val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    private val usersConversations = mutableMapOf<DBUser, MutableSet<DBConversation>>()

    override fun authorization(login: String, password: String): DBUser? {
        val user = manager.loadUser(login) ?: return null
        if (user.password != password) return null
        return user
    }

    override fun getDialogs(user: DBUser): MutableSet<DBConversation> {
        if (!usersConversations.containsKey(user))
            usersConversations.put(user, manager.loadConversations(user).toMutableSet())
        return usersConversations[user] ?: emptySet<DBConversation>().toMutableSet()
    }

    private fun DBUser.getDialog(id_dialog: Int): DBConversation? = getDialogs(this).find { it.id == id_dialog }

    override fun getMessage(user: DBUser, id_dialog: Int, count: Int): List<DBMessage>? {
        return manager.loadMessages(user.getDialog(id_dialog) ?: return null, count)
    }

    override fun signUp(login: String, password: String, name: String): Boolean {
        return manager.loadUser(login) == null && manager.saveUser(DBUser(login = login, password = password, name = name, lastVisit = null))
    }

    override fun setName(user: DBUser, newName: String): Boolean {
        user.name = newName
        return manager.saveUser(user)
    }

    override fun setPassword(user: DBUser, newPassword: String): Boolean {
        user.password = newPassword
        return manager.saveUser(user)
    }

    override fun exit(user: DBUser) {

        usersConversations.remove(user)
    }

    override fun createConversation(user: DBUser, name: String): DBConversation? {
        return user.toConversation(manager.addConversation(name) ?: return null)
    }

    private fun DBUser.toConversation(conversation: DBConversation): DBConversation? {
        if (!manager.addUserToConversation(this, conversation)) return null
        getDialogs(this).add(conversation)
        return conversation
    }

    override fun inviteUser(user: DBUser, id_dialog: Int, loginInvited: String): Boolean {
        manager.loadUser(loginInvited)?.toConversation(user.getDialog(id_dialog) ?: return false) ?: return false
        return true
    }

    override fun kickUser(user: DBUser, id_dialog: Int, loginKicked: String): Boolean {
        val conversation = user.getDialog(id_dialog) ?: return false
        val kickedUser = manager.loadUser(loginKicked) ?: return false

        if (!manager.kickFromConversation(kickedUser, conversation)) return false
        getDialogs(user).remove(conversation)
        return true
    }

    override fun sendMessage(user: DBUser, id_dialog: Int, text: String): Set<DBUser>? {
        val conversation = user.getDialog(id_dialog) ?: return null
        manager.addMessage(user, conversation, text)

        return manager.loadUsers(conversation)
    }
}