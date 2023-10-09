package com.frostedmc.core.api.account.statistics;

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
 * Created by Redraskal_2 on 1/26/2017.
 */
public class StatisticsManager {

    private String STAT_QUERY = "SELECT * FROM `statistics` WHERE uuid=?";
    private String STAT_INSERT = "INSERT INTO `statistics` (uuid, name, value, last_update) VALUES (?, ?, ?, ?)";
    private String STAT_CREATE = "CREATE TABLE IF NOT EXISTS `statistics` (uuid VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, value DOUBLE NOT NULL, last_update VARCHAR(255) NOT NULL)";
    private String STAT_UPDATE = "UPDATE `statistics` SET value=?, last_update=? WHERE uuid=? AND name=?";

    public StatisticsManager() { this.create(); }

    public StatisticProfile fetchProfile(UUID uuid) {
        PreparedStatement statement = this.prepare(this.STAT_QUERY);
        Map<String, Double> map = new HashMap<String, Double>();
        Map<String, Timestamp> lastReset = new HashMap<String, Timestamp>();

        try {
            statement.setString(1, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                map.put(res.getString("name"), res.getDouble("value"));
                lastReset.put(res.getString("name"),
                        new Timestamp(Timestamp.parse(res.getString("last_update"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new StatisticProfile(uuid, map, lastReset);
    }

    public void add(UUID uuid, String name, double amount) {
        StatisticProfile statisticProfile = this.fetchProfile(uuid);
        this.updateStat(uuid, name, (statisticProfile.getStatistic(name)+amount));
    }

    public void subtract(UUID uuid, String name, double amount) {
        StatisticProfile statisticProfile = this.fetchProfile(uuid);
        this.updateStat(uuid, name, (statisticProfile.getStatistic(name)-amount));
    }

    public void updateStat(UUID uuid, String name, double value) {
        PreparedStatement statement2 = this.prepare("SELECT * FROM `statistics` WHERE uuid=? AND name=?");
        try {
            statement2.setString(1, uuid.toString());
            statement2.setString(2, name);
            ResultSet res2 = statement2.executeQuery();
            if(!res2.next()) {
                PreparedStatement statement3 = this.prepare(this.STAT_INSERT);
                statement3.setString(1, uuid.toString());
                statement3.setString(2, name);
                statement3.setDouble(3, value);
                statement3.setString(4, Timestamp.getCurrentTimestamp().toString());
                statement3.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement statement = this.prepare(this.STAT_UPDATE);
            statement.setString(3, uuid.toString());
            statement.setString(4, name);
            statement.setDouble(1, value);
            statement.setString(2, Timestamp.getCurrentTimestamp().toString());
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