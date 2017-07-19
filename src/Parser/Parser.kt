package Parser

interface Parser {
    fun getQuery(text: String): Query?
    fun createQuery(name: String, vararg pair: Pair<String, Any>): Query
    fun createQuery(name: String, pair: Set<Pair<String, Any>>): Query
    fun boolQuery(result: Boolean): Query
}