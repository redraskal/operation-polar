package com.frostedmc.core.sql;

/**
 * Created by Redraskal_2 on 8/24/2016.
 */
public class SQLDetails {

    private String host;
    private int port;

    private String username;
    private String password;
    private String database;

    public SQLDetails(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDatabase() {
        return this.database;
    }
}
