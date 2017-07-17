package FileManager

import source.DB.DBInterface
import source.DB.DBManagerK
import source.DB.DBUser

class FileManagerK {
    private val manager: DBInterface = DBManagerK("C:\\sqlite-dll-win64-x64-3190300\\messenger.db")

    /**
     * Users which now are online
     */
    private val activeUsers = mutableSetOf<DBUser>()
}