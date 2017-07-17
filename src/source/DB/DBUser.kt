package source.DB

import java.sql.Timestamp

data class DBUser(val login: String, var password: String, var name: String, var lastVisit: Timestamp?) {
    override fun equals(other: Any?): Boolean {
        if (other !is DBUser) return false
        return (login == other.login && password == other.password && name == other.name)
    }

    override fun hashCode() = login.hashCode()
}