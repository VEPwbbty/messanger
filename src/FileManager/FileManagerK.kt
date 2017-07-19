package FileManager

import source.DB.*
import source.DB.Interfaces.Conversation
import source.DB.Interfaces.DBInterface
import source.DB.Interfaces.Message
import source.DB.Interfaces.User

class FileManagerK(way: String) : DBManagerK(way), FileInterface {

    private val usersConversations = mutableMapOf<User, MutableSet<Conversation>>()

    override fun authorization(login: String, password: String): User? {
        val user = loadUser(login) ?: return null
        if (user.password != password) return null
        return user
    }

    override fun getDialogs(user: User): MutableSet<Conversation> {
        if (!usersConversations.containsKey(user))
            usersConversations.put(user, user.conversations().toMutableSet())
        return usersConversations[user] ?: emptySet<Conversation>().toMutableSet()
    }

    private fun User.getDialog(id_dialog: Int): Conversation? = getDialogs(this).find { it.id == id_dialog }

    override fun getMessage(user: User, id_dialog: Int, count: Int) = user.getDialog(id_dialog)?.messages(count)

    override fun signUp(login: String, password: String, name: String) = loadUser(login) == null && addUser(login, password, name) != null

    override fun setName(user: User, newName: String) = user.save(user.password, newName)

    override fun setPassword(user: User, newPassword: String) = user.save(newPassword, user.name)

    override fun createConversation(user: User, name: String): Conversation? {
        return user.toConversation(addConversation(name) ?: return null)
    }

    private fun User.toConversation(conversation: Conversation): Conversation? {
        if (!conversation.addUser(this)) return null
        getDialogs(this).add(conversation)
        return conversation
    }

    override fun inviteUser(user: User, id_dialog: Int, loginInvited: String): Boolean {
        loadUser(loginInvited)?.toConversation(user.getDialog(id_dialog) ?: return false) ?: return false
        return true
    }

    override fun kickUser(user: User, id_dialog: Int, loginKicked: String): Boolean {
        val conversation = user.getDialog(id_dialog) ?: return false

        if (!conversation.kickUser(loadUser(loginKicked) ?: return false)) return false
        getDialogs(user).remove(conversation)
        return true
    }

    override fun sendMessage(user: User, id_dialog: Int, text: String): Set<User>? {
        with(user.getDialog(id_dialog) ?: return null) {
            addMessage(user, text)
            return users()
        }
    }

    override fun exit(user: User) {
        usersConversations.remove(user)
    }
}