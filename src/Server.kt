import FileManager.FileInterface
import FileManager.FileManagerK
import Parser.ParserK
import java.nio.channels.SelectionKey
import java.net.InetSocketAddress
import java.nio.channels.ServerSocketChannel
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Selector
import source.DB.DBUser
import java.nio.channels.SocketChannel


class Server(val port: Int) : Runnable {
    //Create ServerSocketChannel, which we will use to communicate with clients
    private val ssc: ServerSocketChannel = ServerSocketChannel.open()
    //Selector will "say" us about new events (such as new message or new connection)
    private val selector: Selector = Selector.open()
    private val buf = ByteBuffer.allocate(256)

    private val authorizedUsers = mutableMapOf<SocketChannel, DBUser>()

    private val manager: FileInterface = FileManagerK()

    init {
        //Set socket on our ServerSocketChannel
        this.ssc.socket().bind(InetSocketAddress(port))
        //Make ssc not blocking
        this.ssc.configureBlocking(false)

        //Selector says us if there will be new client to accepted on our socket
        this.ssc.register(selector, SelectionKey.OP_ACCEPT)
    }

    override fun run() {
        try {
            System.out.println("Server starting on port " + this.port)

            var iter: MutableIterator<SelectionKey>
            var key: SelectionKey
            while (this.ssc.isOpen) {
                selector.select()
                iter = this.selector.selectedKeys().iterator()
                while (iter.hasNext()) {
                    key = iter.next()
                    iter.remove()

                    if (key.isAcceptable) this.handleAccept(key)
                    if (key.isReadable) this.handleRead(key)
                }
            }
        } catch (e: IOException) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:")
            e.printStackTrace()
        }

    }

    private fun handleAccept(key: SelectionKey) {
        val sc = (key.channel() as ServerSocketChannel).accept()
        sc.configureBlocking(false)
        sc.register(selector, SelectionKey.OP_READ)
    }

    private fun SocketChannel.write(text: String) {
        write(ByteBuffer.wrap((text + "\r\n").toByteArray()))
    }

    private fun handleRead(key: SelectionKey) {
        val ch = key.channel() as SocketChannel
        val sb = StringBuilder()

        buf.clear()
        var read = 0
        while (true) {
            read = ch.read(buf)
            if (read <= 0) break
            buf.flip()
            val bytes = ByteArray(buf.limit())
            buf.get(bytes)
            sb.append(String(bytes))
            buf.clear()
        }
//
//        val query = ParserK.getQuery(sb.toString())!!
//        when (query.name) {
//            "login" -> {
//                val user = manager.authorization(query.getString("login"), query.getString("pass"))
//                if (user != null) {
//                    authorizedUsers.put(ch, user)
//                    ch.write(ParserK.createQuery("user", Pair("name", user.name)).text)
//                }
//            }
//            "message" -> {
//                if (authorizedUsers.containsKey(ch)) {
//                    val user = authorizedUsers[ch]!!
//                    val users = manager.sendMessage(user, query.getInt("conv")!!, query.getString("text"))
//                    if (users != null) {
//                        authorizedUsers.forEach { t, u ->
//                            if (users.contains(u))
//                                t.write(ParserK.createQuery("mess",
//                                        Pair("author", user.login),
//                                        Pair("conv", query.getString("conv")),
//                                        Pair("text", query.getString("text"))).text)
//                        }
//                    }
//                }
//            }
//        }
    }
}

fun main(args: Array<String>) {
    val server = Server(23)
    Thread(server).start()
}