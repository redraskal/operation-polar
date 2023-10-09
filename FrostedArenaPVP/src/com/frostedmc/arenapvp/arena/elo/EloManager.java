package com.frostedmc.arenapvp.arena.elo;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 12/31/2016.
 */
public class EloManager {

    private static EloManager instance;
    public static EloManager getInstance() {
        if(instance == null)
            instance = new EloManager();
        return instance;
    }

    private String ELO_QUERY = "SELECT * FROM `arena_elo` WHERE uuid=?";
    private String ELO_INSERT = "INSERT INTO `arena_elo` (uuid, kit, value, last_update) VALUES (?, ?, ?, ?)";
    private String ELO_CREATE = "CREATE TABLE IF NOT EXISTS `arena_elo` (uuid VARCHAR(255) NOT NULL, kit VARCHAR(255) NOT NULL, value INT NOT NULL, last_update VARCHAR(255) NOT NULL)";
    private String ELO_UPDATE = "UPDATE `arena_elo` SET value=?, last_update=? WHERE uuid=? AND kit=?";

    private EloManager() { this.create(); }

    public EloProfile fetchProfile(UUID uuid) {
        PreparedStatement statement = this.prepare(this.ELO_QUERY);
        Map<Kit, Integer> map = new HashMap<Kit, Integer>();
        Map<Kit, Timestamp> lastReset = new HashMap<Kit, Timestamp>();

        try {
            statement.setString(1, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                map.put(Kit.fromName(res.getString("kit")), res.getInt("value"));
                lastReset.put(Kit.fromName(res.getString("kit")),
                        new Timestamp(Timestamp.parse(res.getString("last_update"))));
            }
            for(Kit kit : Kit.values()) {
                if(!map.containsKey(kit)) {
                    try {
                        PreparedStatement statement2 = this.prepare(this.ELO_INSERT);
                        statement2.setString(1, uuid.toString());
                        statement2.setString(2, kit.getName());
                        statement2.setInt(3, 250);
                        statement2.setString(4, Timestamp.getCurrentTimestamp().toString());
                        statement2.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new EloProfile(uuid, map, lastReset);
    }

    public void updateElo(UUID uuid, Kit kit, int value) {
        if(value <= 0) {
            value = -1;
            if(Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.WITHER_HURT, 10, 1);
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lYour elo for " + kit.getName() + " has been lost. You will not be able to play Ranked with this kit for the next 15 minutes."));
                new EloResetRunnable(Bukkit.getPlayer(uuid), kit).runTaskTimer(ArenaPVP.getInstance(), 0, 20L);
            }
        }
        try {
            PreparedStatement statement = this.prepare(this.ELO_UPDATE);
            statement.setString(3, uuid.toString());
            statement.setString(4, kit.getName());
            statement.setInt(1, value);
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
            statement.executeUpdate(this.ELO_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
