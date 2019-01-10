package db

import org.junit.Test

import org.junit.Assert.*

class DataBaseTest {

    @Test
    fun testParams() {
        val db = DataBase(
            connectionString = "jdbc:postgresql://localhost:5432/",
            dbName = "chris",
            schema = "public",
            user = "chris",
            password = "chris"
        )

        db.params.setParam("_cur_ver_", "3")
        println(db.params.getParam("_cur_ver_"))

        db.close()
    }
}