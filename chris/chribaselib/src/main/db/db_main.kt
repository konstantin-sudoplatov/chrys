package db

import java.sql.Connection
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.PreparedStatement

/**
 *          Connection to the database and a set of tables to work with it.
 *  @param connectionString
 *  @param dbName
 *  @param user
 *  @param password
 */
class DataBase(connectionString: String, dbName: String, schema: String, user: String, password: String) {

    /** Postgres connection object */
    private var conn: Connection =
        try {
            DriverManager.getConnection(connectionString + dbName, user, password)
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }

    /** Table of system parameters */
    val params = ParamsTbl(conn, schema, "params")

    fun close() {
        conn.close()
    }
}

/**
 *      System parameters.
 *  @param schema
 *  @param tableName
 */
class ParamsTbl(val conn: Connection, schema: String, private val tableName: String) {

    /**
     *      Get parameter value.
     *  @param parName
     */
    fun getParam(parName: String): String? {
        prepGetParam.setString(1, parName)
        val rs = prepGetParam.executeQuery()
        if(!rs.next())
                throw java.lang.IllegalStateException("There is no parameter with name $parName in the $tableName table.")
        else
            return rs.getString("value")
    }

    /**
     *      Set parameter value.
     *  @param parName
     */
    fun setParam(parName: String, value: String?) {
        prepSetParam.setString(1, value)
        prepSetParam.setString(2, parName)
        val rs = prepSetParam.executeUpdate()
    }

    /** Prepared SQL for funk getParam() */
    private var prepGetParam: PreparedStatement =
        try {
            conn.prepareStatement("""select value from "$schema"."$tableName" where name = ?""")
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }

    /** Prepared SQL for funk setParam() */
    private var prepSetParam: PreparedStatement =
        try {
            conn.prepareStatement("""update "$schema"."$tableName" set value = ? where name = ?""")
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }
}