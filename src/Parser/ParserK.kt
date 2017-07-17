package Parser

object ParserK: Parser {
    override fun getQuery(text: String): Query = QueryK(text)

    override fun createQuery(name: String, vararg pair: Pair<String, String>) =
        getQuery("$name{${pair.joinToString(separator = "") { "${it.first}[${it.second.length}]${it.second}" }}}")
}

private class QueryK(override val text: String): Query {
    override fun getName() = Regex("""[\d\w]+""").find(text, 0)?.value ?: throw IllegalArgumentException()

    private val values = mutableMapOf<String, String>()

    init {
        val fields = Regex("""\{.*}$""").find(text, 0)?.value ?: throw IllegalArgumentException()
        var startIndex = 1

        while (startIndex != fields.length - 1) {
            val brace = Regex("""\[[\d]+]""").find(fields, startIndex) ?: throw IllegalArgumentException()
            val lengthArgument = brace.value.substring(1, brace.value.length - 1).toInt()

            val name = fields.substring(startIndex, brace.range.first)
            startIndex = brace.range.last + lengthArgument + 1
            val argument = fields.substring(brace.range.last + 1, startIndex)
            values.put(name, argument)
        }
    }

    override fun getString(parameter: String) = values[parameter] ?: ""

    override fun getInt(parameter: String) = values[parameter]?.toInt()
}