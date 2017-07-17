package Parser

interface Parser {
    fun getQuery(text: String): Query
    fun createQuery(name: String, vararg pair: Pair<String, String>): Query
}