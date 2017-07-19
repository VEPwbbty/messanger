package Parser

interface Query {
    val text: String
    val name: String
    operator fun get(key: String): String?
}