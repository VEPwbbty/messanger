package FileManager

import source.DB.*

class FileManagerK : FileInterface {

    //TODO Change time of lastVisit in exit fun

    private class Dialog(val conversation: DBConversation) {
        val users = mutableSetOf<DBUser>()
        var messages = mutableListOf<DBMessage>()

        override fun hashCode() = conversation.id
        override fun equals(other: Any?) = (other is Dialog && other.conversation.id == conversation.id)
    }

    private val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    /**
     * Users which now are online
     */
    private val activeUsers = mutableMapOf<DBUser, MutableSet<DBConversation>>()

    private val activeDialog = mutableMapOf<Int, Dialog>()

    override fun authorization(login: String, password: String): DBUser? {
        val user = manager.loadUser(login) ?: return null
        if (user.password != password) return null

        activeUsers.put(user, loadDialogs(user).toMutableSet())
        return user
    }

    private fun loadDialogs(user: DBUser): Set<DBConversation> {
        val conversations = manager.loadConversations(user)
        for (i in conversations) {
            if (!activeDialog.containsKey(i.id)) activeDialog.put(i.id, Dialog(i))
            activeDialog[i.id]!!.users.add(user)
        }
        return conversations
    }

    override fun getDialogs(user: DBUser) = activeUsers[user] ?: emptySet<DBConversation>()

    private fun DBUser.inDialog(id_dialog: Int) =

    override fun getMessage(user: DBUser, id_dialog: Int, count: Int): List<DBMessage>? {
        if (!(activeDialog[id_dialog]?.users?.contains(user) ?: return null)) return null

        val dialog = activeDialog[id_dialog]!!

        if (dialog.messages.size < count)
            dialog.messages = manager.loadMessages(dialog.conversation, count).toMutableList()

        return dialog.messages.subList(dialog.messages.size - count, dialog.messages.size)
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

    override fun exit(user: DBUser): Boolean {
        if (!activeUsers.containsKey(user)) return false
        for ((id) in activeUsers[user]!!)
            if (activeDialog.containsKey(id)) {
                activeDialog[id]!!.users.remove(user)
                if (activeDialog[id]!!.users.isEmpty()) activeDialog.remove(id)
            }
        return true
    }

    override fun createConversation(user: DBUser, name: String): DBConversation? {
        val dialog = Dialog(manager.addConversation(name) ?: return null)
        manager.addUserToConversation(user, dialog.conversation)
        dialog.users.add(user)
        return dialog.conversation
    }

    override fun inviteUser(user: DBUser, id_dialog: Int, loginInvited: String): Boolean {
        if (!(activeDialog[id_dialog]?.users?.contains(user) ?: return false)) return false
        val invitedUser = manager.loadUser(loginInvited) ?: return false

        if (!manager.addUserToConversation(invitedUser, activeDialog[id_dialog]!!.conversation)) return false
        activeDialog[id_dialog]!!.users.add(invitedUser)
        return true
    }

    override fun kickUser(user: DBUser, id_dialog: Int, loginKicked: String): Boolean {
        if (!(activeDialog[id_dialog]?.users?.contains(user) ?: return false)) return false
        val kickedUser = manager.loadUser(loginKicked) ?: return false

        if (!manager.kickFromConversation(kickedUser, activeDialog[id_dialog]!!.conversation)) return false
        activeDialog[id_dialog]!!.users.remove(kickedUser)
        return true
    }

    override fun sendMessage(user: DBUser, id_dialog: Int, text: String): Set<DBUser>? {
        if (!activeDialog.containsKey(id_dialog) || !(activeDialog[id_dialog]?.users?.contains(user) ?: false)) return null
        manager.addMessage(user, activeDialog[id_dialog]!!.conversation, text)

        activeDialog[id_dialog]!!.messages.addAll(getMessages(id_dialog, 1))
        return activeDialog[id_dialog]!!.users
    }

    private fun getMessages(id_dialog: Int, count: Int): List<DBMessage> {
        val currentMessage = manager.loadMessages(activeDialog[id_dialog]!!.conversation, count)
        activeDialog[id_dialog]!!.messages.addAll(currentMessage)
        return currentMessage
    }
}