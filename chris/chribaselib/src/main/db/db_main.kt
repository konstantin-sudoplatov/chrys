package db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 *          Connection to the database and a set of tables to work with it.
 *  @param connectionString
 *  @param dbName
 *  @param user
 *  @param password
 */
class DataBase(connectionString: String, dbName: String, schema: String, user: String, password: String) {
    private var conn: Connection = connectDb(connectionString, dbName, schema, user, password)

    /** Table of system parameters */
    val params = ParamsTbl(conn, schema, "params")

    /** Table of dynamic concepts */
    val concepts = ConceptsTbl(conn, schema, "concepts")

    fun close() {
        conn.close()
    }

    /** Postgres connection object */
    private inline fun connectDb(connectionString: String, dbName: String, schema: String, user: String, password: String):
            Connection
    {
        var con: Connection
        try {
            con = DriverManager.getConnection(connectionString + dbName, user, password)
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }
        return con
    }
}

/**
 *      System parameters.
 *  Table: params
 *  Fields:
 *      "name",      // parameter name
 *      "value",    // parameter value
 *      "description"     // description of the parameter
 *
 *  @param schema
 *  @param tableName
 */
class ParamsTbl(private val conn: Connection, private val schema: String, private val tableName: String) {

    /**
     *      Get parameter value.
     *  @param parName
     */
    fun getParam(parName: String): String? {
        getParamStmt_.setString(1, parName)
        val rs = getParamStmt_.executeQuery()
        if(!rs.next())
                rs.use {throw java.lang.IllegalStateException("There is no parameter with name $parName in the $schema.$tableName table.")}
        else
            return rs.use{ it.getString("value") }
    }

    /**
     *      Set parameter value.
     *  @param parName
     *  @param value
     */
    fun setParam(parName: String, value: String?) {
        SetParamStmt_.setString(1, value)
        SetParamStmt_.setString(2, parName)
        SetParamStmt_.executeUpdate()
    }

    /** Prepared SQL for funk getParam() */
    private var getParamStmt_: PreparedStatement =
        try {
            conn.prepareStatement("""select value from "$schema"."$tableName" where name = ?""")
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }

    /** Prepared SQL for funk setParam() */
    private var SetParamStmt_: PreparedStatement =
        try {
            conn.prepareStatement("""update "$schema"."$tableName" set value = ? where name = ?""")
        } catch (e: SQLException) {
            throw IllegalStateException(e.message)
        }
}