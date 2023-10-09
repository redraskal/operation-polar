package com.frostedmc.core.api.account.punish;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/24/2017.
 */
public class GlacierPunishmentManager {

    private String PUNISH_QUERY = "SELECT * FROM `glacier` WHERE uuid=?";
    private String PUNISH_INSERT = "INSERT INTO `glacier` (uuid, reason) VALUES (?, ?)";
    private String PUNISH_CREATE = "CREATE TABLE IF NOT EXISTS `glacier` (uuid VARCHAR(255) NOT NULL, reason VARCHAR(255) NOT NULL)";

    public GlacierPunishmentManager() { this.create(); }

    public String parsePunishmentRecord(UUID uuid) {
        try {
            ResultSet resultSet = this.queryPunishmentRecord(uuid);
            if(resultSet.next()) {
                return resultSet.getString("reason");
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public ResultSet queryPunishmentRecord(UUID uuid) {
        PreparedStatement statement = this.prepare(this.PUNISH_QUERY);

        try {
            statement.setString(1, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet result = statement.executeQuery();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean hasPunishment(UUID uuid) {
        try {
            ResultSet result = queryPunishmentRecord(uuid);

            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(UUID uuid, String reason) {
        PreparedStatement statement = this.prepare(this.PUNISH_INSERT);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, reason);
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
            statement.executeUpdate(this.PUNISH_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}