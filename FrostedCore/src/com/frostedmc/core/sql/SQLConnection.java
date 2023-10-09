package com.frostedmc.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Redraskal_2 on 8/24/2016.
 */
public class SQLConnection {

    private SQLDetails details;
    private Connection connection;
    public boolean safeClose = false;

    public SQLConnection(SQLDetails sqlDetails) {
        this.details = sqlDetails;
    }

    public boolean openConnection() {
        try {
            if(connection != null) {
                if(!connection.isClosed()) {
                    return false;
                }
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://"
                                + details.getHost()
                                + ":"
                                + details.getPort()
                                + "/"
                                + details.getDatabase()
                                + "?autoReconnect=true",
                        details.getUsername(),
                        details.getPassword());
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed() || !connection.isValid(2))
                closeConnection();
                openConnection();
        } catch (SQLException e) { e.printStackTrace(); }
        return this.connection;
    }

    public boolean closeConnection() {
        try {
            if(connection != null) {
                if(connection.isClosed()) {
                    return false;
                }
            } else {
                return false;
            }

            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public SQLDetails getDetails() {
        return details;
    }
}
