package Parser

object ParserK : Parser {
    override fun getQuery(message: String): Query? {
        val text = message.replace("\r", "").replace("\n", "")
        if (!text.matches(Regex("""[\d\w]+\{([\d\w]+\[\d+].*)+}"""))) return null
        val resultQuery = MapQuery(text)

        resultQuery.name = Regex("[\\d\\w]+").find(text, 0)?.value ?: return null

        val fields = Regex("\\{.*}\$").find(text, 0)?.value ?: return null
        var startIndex = 1

        while (fields[startIndex] != '}') {
            val brace = Regex("""\[[\d]+]""").find(fields, startIndex) ?: return null
            val lengthArgument = brace.value.substring(1, brace.value.length - 1).toInt()

            if (brace.range.last + 2 + lengthArgument > fields.length) return null

            val name = fields.substring(startIndex, brace.range.first)
            startIndex = brace.range.last + lengthArgument + 1
            val argument = fields.substring(brace.range.last + 1, startIndex)
            resultQuery[name] = argument
        }

        return resultQuery
    }

    override fun createQuery(name: String, vararg pair: Pair<String, Any>): Query = ParserK.createQuery(name, pair.toSet())

    override fun createQuery(name: String, pair: Set<Pair<String, Any>>): Query {
        val query = MapQuery("$name{${pair.joinToString(separator = "") { "${it.first}[${it.second.toString().length}]${it.second}" }}}")
        query.name = name
        pair.forEach { query[it.first] = it.second.toString() }
        return query
    }

    override fun boolQuery(result: Boolean) = createQuery("answer", Pair("res", if (result) "success" else "fail"))
}

private class MapQuery(override val text: String) : Query {
    val values = mutableMapOf<String, String>()

    override var name: String = ""

    override fun get(key: String) = values[key]

    operator fun set(key: String, value: String) {
        values[key] = value
    }
}

fun main(args: Array<String>) {
    println(ParserK.createQuery("messOfConv", Pair("dial", "6"), Pair("count", "20")).text)
}