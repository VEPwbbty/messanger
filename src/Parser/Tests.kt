package Parser

import junit.framework.Assert.assertEquals
import org.junit.Test

class Tests {

    @Test
    fun query() {
        val query = ParserK.getQuery("""object{var1[28]hi.  |cu cu|var2[2]huvar3[2]hov[3]2345}""")!!

        assertEquals("object", query.name)
        assertEquals("hi. my name is john! |cu cu|", query["var1"])
        assertEquals("hu", query["var2"])
        assertEquals("ho", query["var3"])
        assertEquals(2345, query["v"]?.toInt())
    }

    @Test
    fun createQuery() {
        val query = "device{a[2]web[4]qaswy[1]2tamapor[5]12345}"
        assertEquals(query, ParserK.createQuery("device", Pair("a","we"), Pair("b", "qasw"), Pair("y", "2"), Pair("tamapor", "12345")).text)
    }
}

