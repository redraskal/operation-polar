package com.frostedmc.core.api.account;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 9/13/2016.
 */
public class PurchaseManager {

    private String PURCHASES_QUERY = "SELECT * FROM `purchases` WHERE uuid=? AND item_id=?";
    private String PURCHASES_INSERT = "INSERT INTO `purchases` (uuid, transaction_id, item_id, timestamp) VALUES (?, ?, ?, ?)";
    private String PURCHASES_CREATE = "CREATE TABLE IF NOT EXISTS `purchases` (uuid VARCHAR(255) NOT NULL, transaction_id VARCHAR(255) NOT NULL, item_id VARCHAR(255) NOT NULL, timestamp VARCHAR(255) NOT NULL)";

    public PurchaseManager() {
        this.create();
    }

    public ResultSet queryTransaction(UUID uuid, String item_id) {
        PreparedStatement statement = this.prepare(this.PURCHASES_QUERY);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, item_id);
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

    public boolean exists(UUID uuid, String item_id) {
        ResultSet resultSet = this.queryTransaction(uuid, item_id);

        try {
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean createTransaction(UUID uuid, String transaction_id, String item_id) {
        PreparedStatement statement = this.prepare(this.PURCHASES_INSERT);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, transaction_id);
            statement.setString(3, item_id);
            statement.setString(4, Timestamp.getCurrentTimestamp().toString());
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
            statement.executeUpdate(this.PURCHASES_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}