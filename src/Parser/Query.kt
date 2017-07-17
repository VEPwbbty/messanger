package Parser

interface Query {
    val text: String
    fun getName(): String
    fun getString(parameter: String): String?
    fun getInt(parameter: String): Int?
}