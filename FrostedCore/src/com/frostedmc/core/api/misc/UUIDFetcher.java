package com.frostedmc.core.api.misc;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/28/2017.
 */
public class UUIDFetcher {

    private String UUID_QUERY = "SELECT * FROM `uuid_cache` WHERE username=?";
    private String UUID_INSERT = "INSERT INTO `uuid_cache` (username, uuid) VALUES (?, ?)";
    private String UUID_CREATE = "CREATE TABLE IF NOT EXISTS `uuid_cache` (username VARCHAR(16) NOT NULL, uuid VARCHAR(255) NOT NULL)";

    private Map<String, UUID> uuidCache = new HashMap<String, UUID>();

    public UUIDFetcher() { this.create(); }

    public ResultSet queryAccountDetails(String username) {
        PreparedStatement statement = this.prepare(this.UUID_QUERY);

        try {
            statement.setString(1, username);
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

    public ResultSet queryAccountDetails(UUID uuid) {
        PreparedStatement statement = this.prepare("SELECT * FROM `uuid_cache` WHERE uuid=?");

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

    public boolean register(String username, UUID uuid) {
        PreparedStatement statement = this.prepare(this.UUID_INSERT);

        try {
            statement.setString(1, username);
            statement.setString(2, uuid.toString());
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

    public UUID parseDetails(String username) {
        if(uuidCache.containsKey(username)) return uuidCache.get(username);
        try {
            ResultSet result = queryAccountDetails(username);

            if(result.next()) {
                uuidCache.put(username, UUID.fromString(result.getString("uuid")));
                return uuidCache.get(username);
            } else {
                AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(username);
                if(accountDetails != null) {
                    uuidCache.put(username, accountDetails.getUUID());
                    register(username, uuidCache.get(username));
                }
                if(uuidCache.containsKey(username)) {
                    return uuidCache.get(username);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String fix(String uuid) {
        StringBuffer builder = new StringBuffer(uuid);
        builder.insert(8, "-");
        builder = new StringBuffer(builder.toString());
        builder.insert(13, "-");
        builder = new StringBuffer(builder.toString());
        builder.insert(18, "-");
        builder = new StringBuffer(builder.toString());
        builder.insert(23, "-");
        return builder.toString();
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
            statement.executeUpdate(this.UUID_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}