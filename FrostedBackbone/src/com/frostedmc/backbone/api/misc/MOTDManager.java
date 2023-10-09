package com.frostedmc.backbone.api.misc;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class MOTDManager {

    private String MOTD_QUERY = "SELECT * FROM `motd`";
    private String MOTD_INSERT = "INSERT INTO `motd` (id, value) VALUES (?, ?)";
    private String MOTD_CREATE = "CREATE TABLE IF NOT EXISTS `motd` (id VARCHAR(255) NOT NULL, value VARCHAR(255) NOT NULL)";
    private String MOTD_UPDATE = "UPDATE `motd` SET value=?";

    private String motdCache = "";

    public MOTDManager() { this.create(); }

    public ResultSet queryMOTD() {
        PreparedStatement statement = this.prepare(this.MOTD_QUERY);

        try {
            ResultSet result = statement.executeQuery();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean exists() {
        try {
            ResultSet result = queryMOTD();

            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(String value) {
        if(exists()) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.MOTD_INSERT);

        try {
            statement.setString(1, "text");
            statement.setString(2, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(String value) {
        if(!exists()) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.MOTD_UPDATE);

        try {
            statement.setString(1, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.motdCache = value;
        return true;
    }

    public String parseDetails() {
        if(this.motdCache.isEmpty()) {
            try {
                ResultSet result = queryMOTD();

                if(result.next()) {
                    this.motdCache = result.getString("value");
                    return this.motdCache;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return this.motdCache;
        }

        return null;
    }

    public void updateCache() {
        this.motdCache = ""; parseDetails();
    }

    private PreparedStatement prepare(String statement) {
        try {
            return Core.getInstance().getSQLConnection().getConnection().prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void create() {
        try {
            Statement statement = Core.getInstance().getSQLConnection().getConnection().createStatement();
            statement.executeUpdate(this.MOTD_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
