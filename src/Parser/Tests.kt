package Parser

import junit.framework.Assert.assertEquals
import org.junit.Test

class Tests {

    @Test
    fun query() {
        val query = ParserK().getQuery("""object{var1[28]hi. my name is john! |cu cu|var2[2]huvar3[2]hov[4]2345}""")
        assertEquals("object", query.getName())
        assertEquals("hi. my name is john! |cu cu|", query.getString("var1"))
        assertEquals("hu", query.getString("var2"))
        assertEquals("ho", query.getString("var3"))
        assertEquals(2345, query.getInt("v"))
    }

    @Test
    fun createQuery() {
        val query = "device{a[2]web[4]qaswy[1]2tamapor[5]12345}"
        assertEquals(query, ParserK().createQuery("device", Pair("a","we"), Pair("b", "qasw"), Pair("y", "2"), Pair("tamapor", "12345")).text)
    }
}