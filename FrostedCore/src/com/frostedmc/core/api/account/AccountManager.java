package com.frostedmc.core.api.account;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public class AccountManager {

    private String ACCOUNT_QUERY = "SELECT * FROM `account` WHERE uuid=?";
    private String ACCOUNT_INSERT = "INSERT INTO `account` (uuid, username, rank, icicles, firstjoined, lastseen, timeonline, ip) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private String ACCOUNT_CREATE = "CREATE TABLE IF NOT EXISTS `account` (uuid VARCHAR(255) NOT NULL, username VARCHAR(16) NOT NULL, rank INT NOT NULL, icicles INT NOT NULL, firstjoined VARCHAR(255) NOT NULL, lastseen VARCHAR(255) NOT NULL, timeonline DOUBLE NOT NULL, ip VARCHAR(255) NOT NULL)";
    private String ACCOUNT_UPDATE = "UPDATE `account` SET # = ? WHERE uuid=?";

    private Map<UUID, AccountDetails> accountCache = new HashMap<UUID, AccountDetails>();

    public AccountManager() { this.create(); }

    public ResultSet queryAccountDetails(String username) {
        PreparedStatement statement = this.prepare("SELECT * FROM `account` WHERE username=?");

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

    public ResultSet queryAccountDetailsAddress(String ip_address) {
        PreparedStatement statement = this.prepare("SELECT * FROM `account` WHERE ip=?");

        try {
            statement.setString(1, ip_address);
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
        PreparedStatement statement = this.prepare(this.ACCOUNT_QUERY);

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

    public boolean isRegistered(UUID uuid) {
        try {
            ResultSet result = queryAccountDetails(uuid);

            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(UUID uuid, String username, String ipAddress) {
        if(isRegistered(uuid)) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.ACCOUNT_INSERT);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, username);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setString(5, Timestamp.getCurrentTimestamp().toString());
            statement.setString(6, Timestamp.getCurrentTimestamp().toString());
            statement.setDouble(7, 0);
            statement.setString(8, ipAddress);
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

    public boolean update(UUID uuid, UpdateDetails updateDetails) {
        if(!isRegistered(uuid)) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.ACCOUNT_UPDATE.replace("#", updateDetails.getUpdateType().getColumn()));

        try {
            statement.setString(2, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(updateDetails.getUpdateType().getType() == 0) {
            try {
                statement.setString(1, (String) updateDetails.getValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(updateDetails.getUpdateType().getType() == 1) {
            try {
                statement.setInt(1, (Integer) updateDetails.getValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(updateDetails.getUpdateType().getType() == 2) {
            try {
                statement.setDouble(1, (Double) updateDetails.getValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.refreshCache(uuid);

        return true;
    }

    public AccountDetails parseDetails(UUID uuid) {
        if(!this.accountCache.containsKey(uuid)) {
            try {
                ResultSet result = queryAccountDetails(uuid);

                if(result.next()) {
                    this.accountCache.put(uuid, new AccountDetails(UUID.fromString(result.getString("uuid")),
                            result.getString("username"),
                            Rank.parse(result.getInt("rank")),
                            result.getInt("icicles"),
                            new Timestamp(Timestamp.parse(result.getString("firstjoined"))),
                            new Timestamp(Timestamp.parse(result.getString("lastseen"))),
                            result.getDouble("timeonline"),
                            result.getString("ip")));
                    return this.accountCache.get(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return this.accountCache.get(uuid);
        }

        return null;
    }

    public AccountDetails parseDetails(String username) {
        try {
            ResultSet result = queryAccountDetails(username);

            if(result.next()) {
                return new AccountDetails(UUID.fromString(result.getString("uuid")),
                        result.getString("username"),
                        Rank.parse(result.getInt("rank")),
                        result.getInt("icicles"),
                        new Timestamp(Timestamp.parse(result.getString("firstjoined"))),
                        new Timestamp(Timestamp.parse(result.getString("lastseen"))),
                        result.getDouble("timeonline"),
                        result.getString("ip"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean refreshCache(UUID uuid) {
        if(!accountCache.containsKey(uuid))
            return false;
        accountCache.remove(uuid);
        return true;
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
            statement.executeUpdate(this.ACCOUNT_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
