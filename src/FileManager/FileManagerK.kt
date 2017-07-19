package FileManager

import source.DB.*
import source.DB.Interfaces.Conversation
import source.DB.Interfaces.DBInterface
import source.DB.Interfaces.Message
import source.DB.Interfaces.User

class FileManagerK : FileInterface {
    override fun authorization(login: String, password: String): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDialogs(user: User): Set<Conversation> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessage(user: User, id_dialog: Int, count: Int): List<Message>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signUp(login: String, password: String, name: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setName(user: User, newName: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPassword(user: User, newPassword: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createConversation(user: User, name: String): Conversation? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun inviteUser(user: User, id_dialog: Int, loginInvited: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun kickUser(user: User, id_dialog: Int, loginKicked: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendMessage(user: User, id_dialog: Int, text: String): Set<User>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exit(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*private val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    private val usersConversations = mutableMapOf<User, MutableSet<Conversation>>()

    override fun authorization(login: String, password: String): User? {
        val user = manager.loadUser(login) ?: return null
        if (user.password != password) return null
        return user
    }

    override fun getDialogs(user: User): MutableSet<Conversation> {
        if (!usersConversations.containsKey(user))
            usersConversations.put(user, user.conversations().toMutableSet())
        return usersConversations[user] ?: emptySet<Conversation>().toMutableSet()
    }

    private fun User.getDialog(id_dialog: Int): Conversation? = getDialogs(this).find { it.id == id_dialog }

    override fun getMessage(user: User, id_dialog: Int, count: Int): List<DBMessage>? {
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
*/}