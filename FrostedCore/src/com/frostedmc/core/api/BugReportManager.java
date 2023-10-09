package com.frostedmc.core.api;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class BugReportManager {

    private String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS `bug_reports` (uuid VARCHAR(255) NOT NULL, topic VARCHAR(255) NOT NULL, message VARCHAR(255) NOT NULL, timestamp TIMESTAMP)";
    private String REPORT_CREATE = "INSERT INTO `bug_reports` (uuid, topic, message, timestamp) VALUES (?, ?, ?, ?)";

    public BugReportManager() { this.create(); }

    public void createReport(UUID uuid, String topic, String message) {
        PreparedStatement statement = this.prepare(this.REPORT_CREATE);
        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, topic);
            statement.setString(3, message);
            statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            statement.executeUpdate(this.TABLE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}