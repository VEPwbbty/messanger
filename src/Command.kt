import FileManager.FileInterface
import FileManager.FileManagerK
import Parser.Parser
import Parser.ParserK
import Parser.Query
import source.DB.Interfaces.User

//TODO enum for types of commands
fun getCommand(idChannel: Int, query: Query): Command? =
        try {
            when (query.name) {
                "login" -> Command.login(idChannel, query)
                "messOfConv" -> Command.messages(idChannel, query)
                "mess" -> Command.message(idChannel, query)
                "convs" -> Command.conversations(idChannel)
                "sign" -> Command.signUp(idChannel, query)
                "crCon" -> Command.createDialog(idChannel, query)
                "inv" -> Command.addUserToConversation(idChannel, query)
                "kck" -> Command.kickUserFromConversation(idChannel, query)
                "chname" -> Command.changeName(idChannel, query)
                "chpass" -> Command.changePassword(idChannel, query)
                "ex" -> Command.exit(idChannel)
                else -> null
            }
        } catch (e: IllegalArgumentException) {
            null
        }

sealed class Command() {

    companion object {
        protected val manager: FileInterface = FileManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")
        protected val authorizedUsers = mutableMapOf<Int, User>()
    }

    abstract fun perform(): Map<Int, List<String>>

    protected fun createMap(channel: Int, nameOfQuery: String, vararg pair: Pair<String, Any>) =
            mapOf(Pair(channel, listOf(ParserK.createQuery(nameOfQuery, pair.toSet()).text)))

    protected fun createBooleanMap(channel: Int, result: Boolean) =
            mapOf(Pair(channel, listOf(ParserK.boolQuery(result).text)))

    class login(val idChannel: Int, query: Query) : Command() {
        val login = query["login"] ?: throw IllegalArgumentException()
        val password = query["pass"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            val user = manager.authorization(login, password)
            if (user != null) {
                authorizedUsers.put(idChannel, user)
                //TODO add sending conversations of this user
                return createMap(idChannel, "user", Pair("name", user.name))
            }
            //TODO send about wrong password or login
            return emptyMap()
        }
    }

    class signUp(val idChannel: Int, query: Query) : Command() {
        val login = query["login"] ?: throw IllegalArgumentException()
        val password = query["pass"] ?: throw IllegalArgumentException()
        val name = query["name"] ?: throw IllegalArgumentException()

        override fun perform() = createBooleanMap(idChannel, manager.signUp(login, password, name))
    }

    class createDialog(val idChannel: Int, query: Query) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val name = query["name"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            val conversation = manager.createConversation(user, name) ?: return emptyMap()
            return createMap(idChannel, "dialog", Pair("id", conversation.id), Pair("name", conversation.name))
        }
    }

    class addUserToConversation(val idChannel: Int, query: Query) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val id = query["id_conv"]?.toInt() ?: throw IllegalArgumentException()
        val userID = query["usid"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> = createBooleanMap(idChannel, manager.inviteUser(user, id, userID))
    }

    class changeName(val idChannel: Int, query: Query): Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val name = query["name"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> = createBooleanMap(idChannel, manager.setName(user, name))
    }

    class changePassword(val idChannel: Int, query: Query): Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val password = query["pass"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> = createBooleanMap(idChannel, manager.setPassword(user, password))
    }

    class kickUserFromConversation(val idChannel: Int, query: Query) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val id = query["id_conv"]?.toInt() ?: throw IllegalArgumentException()
        val userID = query["usid"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> = createBooleanMap(idChannel, manager.kickUser(user, id, userID))
    }


    class exit(val idChannel: Int) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            manager.exit(user)
            authorizedUsers.remove(idChannel)
            return emptyMap()
        }
    }

    class messages(val idChannel: Int, query: Query) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val id_dialog = query["dial"]?.toInt() ?: throw IllegalArgumentException()
        val count = query["count"]?.toInt() ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            //TODO send failure
            val messages = manager.getMessage(user, id_dialog, count) ?: return mapOf(Pair(idChannel, emptyList()))
            return mapOf(Pair(idChannel, messages.map {
                ParserK.createQuery("mess",
                        Pair("author", it.login_user),
                        Pair("conv", it.id_conv.toString()),
                        Pair("text", it.text)).text
            }))
        }
    }

    class conversations(val idChannel: Int) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            val dialogs = manager.getDialogs(user)
            return mapOf(Pair(idChannel, dialogs.map {
                ParserK.createQuery("conversation",
                        Pair("id", it.id.toString()),
                        Pair("name", it.name)).text
            }))
        }
    }

    class message(val idChannel: Int, query: Query) : Command() {
        val user: User = authorizedUsers[idChannel] ?: throw IllegalArgumentException()
        val id_dialog = query["dial"]?.toInt() ?: throw IllegalArgumentException()
        val text = query["text"] ?: throw IllegalArgumentException()

        override fun perform(): Map<Int, List<String>> {
            val users = manager.sendMessage(user, id_dialog, text)
            val map = mutableMapOf<Int, List<String>>()
            if (users != null)
                authorizedUsers.forEach { t, u ->
                    if (users.contains(u))
                        map.put(t, listOf(ParserK.createQuery("mess",
                                Pair("author", user.login),
                                Pair("conv", id_dialog.toString()),
                                Pair("text", text)).text))
                }
            return map
        }
    }
}