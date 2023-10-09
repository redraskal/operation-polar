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
 * Created by Redraskal_2 on 1/21/2017.
 */
public class TitleManager {

    private String TITLE_QUERY = "SELECT * FROM `titles` WHERE uuid=?";
    private String TITLE_INSERT = "INSERT INTO `titles` (uuid, title) VALUES (?, ?)";
    private String TITLE_CREATE = "CREATE TABLE IF NOT EXISTS `titles` (uuid VARCHAR(255) NOT NULL, title VARCHAR(16) NOT NULL)";
    private String TITLE_UPDATE = "UPDATE `titles` SET title=? WHERE uuid=?";

    private Map<UUID, String> accountCache = new HashMap<UUID, String>();

    public TitleManager() { this.create(); }

    public ResultSet queryTitle(UUID uuid) {
        PreparedStatement statement = this.prepare(this.TITLE_QUERY);

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

    public boolean hasTitle(UUID uuid) {
        try {
            ResultSet result = queryTitle(uuid);
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(UUID uuid, String title) {
        if(hasTitle(uuid)) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.TITLE_INSERT);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, title);
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

    public boolean update(UUID uuid, String title) {
        if(!hasTitle(uuid)) {
            return false;
        }

        PreparedStatement statement = this.prepare(this.TITLE_UPDATE);

        try {
            statement.setString(2, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.setString(1, title);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String parseDetails(UUID uuid) {
        if(!this.accountCache.containsKey(uuid)) {
            try {
                ResultSet result = queryTitle(uuid);

                if(result.next()) {
                    return result.getString("title");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return this.accountCache.get(uuid);
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
            statement.executeUpdate(this.TITLE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}