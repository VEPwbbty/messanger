package source.DB

import java.sql.Timestamp

data class DBMessage(val login_user: String, val id_conv: Int, var text: String, val time: Timestamp) {}