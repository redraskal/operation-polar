package com.frostedmc.core.api.misc;

import com.frostedmc.core.Core;
import com.frostedmc.core.utils.Utils;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

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
public class GameProfileFetcher {

    private String UUID_QUERY = "SELECT * FROM `profile_cache` WHERE uuid=?";
    private String UUID_INSERT = "INSERT INTO `profile_cache` (uuid, textures) VALUES (?, ?)";
    private String UUID_CREATE = "CREATE TABLE IF NOT EXISTS `profile_cache` (uuid VARCHAR(255) NOT NULL, textures VARCHAR(6000) NOT NULL)";

    private Map<UUID, String> uuidCache = new HashMap<UUID, String>();

    public GameProfileFetcher() { this.create(); }

    public ResultSet queryAccountDetails(UUID uuid) {
        PreparedStatement statement = this.prepare(this.UUID_QUERY);

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

    public boolean register(UUID uuid, String textures) {
        PreparedStatement statement = this.prepare(this.UUID_INSERT);

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, textures);
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

    public String parseDetails(UUID uuid) {
        if(uuidCache.containsKey(uuid)) return uuidCache.get(uuid);
        try {
            ResultSet result = queryAccountDetails(uuid);

            if(result.next()) {
                uuidCache.put(uuid, result.getString("textures"));
                return uuidCache.get(uuid);
            } else {
                if(Bukkit.getPlayer(uuid) != null) {
                    uuidCache.put(uuid,
                            ((CraftPlayer) Bukkit.getPlayer(uuid))
                                    .getProfile().getProperties().get("textures")
                                    .stream().findFirst().get().getValue());
                    register(uuid, uuidCache.get(uuid));
                } else {
                    try {
                        JsonElement jsonElement = Utils.fetchJSON("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=true");
                        if(jsonElement.getAsJsonObject().get("id") != null) {
                            uuidCache.put(uuid, jsonElement.getAsJsonObject().get("properties")
                                    .getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString());
                            register(uuid, uuidCache.get(uuid));
                        }
                    } catch (Exception e) {}
                }
                if(uuidCache.containsKey(uuid)) {
                    return uuidCache.get(uuid);
                } else {
                    return "";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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