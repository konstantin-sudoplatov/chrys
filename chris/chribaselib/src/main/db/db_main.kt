package db

import java.sql.Connection
import java.sql.SQLException
import java.sql.DriverManager



/**
 *          Connect to the database.
 *  @param connectionString
 *  @param dbName
 *  @param user
 *  @param password
 */
fun connectToDb(connectionString: String, dbName: String, user: String, password: String): Connection {
    var conn: Connection
    try {
        conn = DriverManager.getConnection(connectionString + dbName, user, password)
    } catch (e: SQLException) {
        throw IllegalStateException(e.message)
    }

    return conn
}