package source.DB.Interfaces

import java.sql.Timestamp

interface Message {
    val login_user: String
    val id_conv: Int
    val text: String
    val time: Timestamp
}