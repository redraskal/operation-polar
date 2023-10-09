package com.frostedmc.backbone.api.misc;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class MaintenanceManager {

    private String MAINTENANCE_QUERY = "SELECT * FROM `maintenance` WHERE id='toggle'";
    private String MAINTENANCE_INSERT = "INSERT INTO `maintenance` (id, value) VALUES (?, ?)";
    private String MAINTENANCE_CREATE = "CREATE TABLE IF NOT EXISTS `maintenance` (id VARCHAR(255) NOT NULL, value VARCHAR(255) NOT NULL)";
    private String MAINTENANCE_UPDATE = "UPDATE `maintenance` SET value=? WHERE id=?";

    private boolean initialized = false;
    private boolean maintenanceCache = false;

    public MaintenanceManager() { this.create(); }

    public ResultSet queryMaintenance() {
        PreparedStatement statement = this.prepare(this.MAINTENANCE_QUERY);

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
            ResultSet result = queryMaintenance();

            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(boolean value) {
        if(exists()) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.MAINTENANCE_INSERT);

        try {
            statement.setString(1, "toggle");
            statement.setString(2, "" + value);
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

    public boolean update(boolean value) {
        if(!exists()) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.MAINTENANCE_UPDATE);

        try {
            statement.setString(1, "toggle");
            statement.setString(2, "" + value);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.maintenanceCache = value;
        return true;
    }

    public boolean parseDetails(boolean force) {
        if(!this.initialized || force) {
            try {
                ResultSet result = queryMaintenance();

                if(result.next()) {
                    this.initialized = true;
                    this.maintenanceCache = Boolean.valueOf(result.getString("value"));
                    return this.maintenanceCache;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return this.maintenanceCache;
        }

        return false;
    }

    public void updateCache() {
        this.parseDetails(true);
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
            statement.executeUpdate(this.MAINTENANCE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}