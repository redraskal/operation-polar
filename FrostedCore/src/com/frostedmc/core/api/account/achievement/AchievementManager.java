package com.frostedmc.core.api.account.achievement;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Timestamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class AchievementManager {

    private String STAT_QUERY = "SELECT * FROM `achievements` WHERE uuid=?";
    private String STAT_INSERT = "INSERT INTO `achievements` (uuid, name, description, timestamp) VALUES (?, ?, ?, ?)";
    private String STAT_CREATE = "CREATE TABLE IF NOT EXISTS `achievements` (uuid VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, timestamp VARCHAR(255) NOT NULL)";

    public AchievementManager() { this.create(); }

    public AchievementProfile fetchProfile(UUID uuid) {
        PreparedStatement statement = this.prepare(this.STAT_QUERY);
        Map<Achievement, Timestamp> map = new HashMap<Achievement, Timestamp>();

        try {
            statement.setString(1, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                map.put(new Achievement(res.getString("name"), res.getString("description")), new Timestamp(Timestamp.parse(res.getString("timestamp"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new AchievementProfile(uuid, map);
    }

    public void add(UUID uuid, Achievement achievement) {
        try {
            PreparedStatement statement = this.prepare(this.STAT_INSERT);
            statement.setString(1, uuid.toString());
            statement.setString(2, achievement.getTitle());
            statement.setString(3, achievement.getDescription());
            statement.setString(4, Timestamp.getCurrentTimestamp().toString());
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
            statement.executeUpdate(this.STAT_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
