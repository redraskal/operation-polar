package com.frostedmc.kingdoms.backend;

import com.frostedmc.kingdoms.Kingdoms;

import java.io.File;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class PlayerDataManager {

    private static PlayerDataManager instance;

    public static PlayerDataManager getInstance() {
        if(instance == null)
            instance = new PlayerDataManager();
        return instance;
    }

    private PlayerDataManager() {}

    public PlayerData fetchPlayerData(UUID uuid) {
        Configuration configuration = this.getConfiguration(uuid);
        Kingdom kingdom = null;
        if(!configuration.exists()) {
            configuration.get().put("gold", 0);
            configuration.get().put("currentKingdom", "");
            configuration.save();
        }
        String tempKingdom = (String) configuration.get().get("currentKingdom");
        if(tempKingdom != null &&
                !tempKingdom.isEmpty()) {
            kingdom = KingdomDataManager.getInstance().fetchKingdomData(UUID.fromString(tempKingdom));
        }
        return new PlayerData(uuid, Utils.intValue(configuration.get().get("gold")), kingdom);
    }

    public Configuration getConfiguration(UUID uuid) {
        return new Configuration(new File(this.getDataFolder().getPath() + "/" + uuid.toString() + ".json"));
    }

    public File getDataFolder() {
        return new File(Kingdoms.getInstance().getDataFolder().getPath() + "/player_data/");
    }
}