package com.frostedmc.kingdoms.backend;

import com.frostedmc.kingdoms.Kingdoms;
import org.bukkit.Chunk;

import java.io.File;
import java.util.*;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class KingdomDataManager {

    private static KingdomDataManager instance;

    public static KingdomDataManager getInstance() {
        if(instance == null)
            instance = new KingdomDataManager();
        return instance;
    }

    private Map<Chunk, Kingdom> chunkCache = new HashMap<Chunk, Kingdom>();

    private KingdomDataManager() {}

    public Kingdom fetchKingdomData(UUID uuid) {
        Configuration configuration = this.getConfiguration(uuid);
        return new Kingdom(uuid, (String) configuration.get().get("name"), Utils.intValue(configuration.get().get("resourcePoints")),
                Utils.convertChunks((List<String>) configuration.get().get("chunks")), Utils.convertMembers((List<String>) configuration.get().get("members")));
    }

    public void reloadCache() {
        Kingdoms.getInstance().getLogger().info("[Cache] Reloading the chunk cache...");
        this.chunkCache.clear();
        List<Kingdom> kingdoms = this.loadKingdoms();
        for(Kingdom kingdom : kingdoms) {
            Kingdoms.getInstance().getLogger().info("[Cache] Loading " + kingdom.getChunks().size() + " chunks for [" + kingdom.getName() + "]...");
            for(Chunk chunk : kingdom.getChunks()) {
                this.chunkCache.put(chunk, kingdom);
            }
        }
        Kingdoms.getInstance().getLogger().info("[Cache] The chunk cache has been reloaded.");
    }

    public Kingdom fetchKingdom(Chunk chunk) {
        for(Map.Entry<Chunk, Kingdom> entry : chunkCache.entrySet()) {
            if(entry.getKey().getX() == chunk.getX()
                    && entry.getKey().getZ() == chunk.getZ()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Kingdom fetchKingdom(UUID player) {
        for(Kingdom kingdom : this.loadKingdoms()) {
            if(kingdom.getMembers().contains(player)) {
                return kingdom;
            }
        }
        return null;
    }

    public boolean exists(String name) {
        for(Kingdom kingdom : this.loadKingdoms()) {
            if(kingdom.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean createKingdom(String name) {
        if(exists(name))
            return false;
        Kingdoms.getInstance().getLogger().info("[Cache] Creating [" + name + "]...");
        Configuration configuration = this.getConfiguration(UUID.randomUUID());
        configuration.get().put("name", name);
        configuration.get().put("resourcePoints", 0);
        configuration.get().put("members", Arrays.asList());
        configuration.get().put("chunks", Arrays.asList());
        configuration.save();
        return true;
    }

    public List<Kingdom> loadKingdoms() {
        List<Kingdom> tempCache = new ArrayList<Kingdom>();
        for(File file : this.getDataFolder().listFiles()) {
            if(!file.isDirectory()) {
                tempCache.add(fetchKingdomData(UUID.fromString(file.getName().split(".")[0])));
            }
        }
        return tempCache;
    }

    public Configuration getConfiguration(UUID uuid) {
        return new Configuration(new File(this.getDataFolder().getPath() + "/" + uuid.toString() + ".json"));
    }

    public File getDataFolder() {
        return new File(Kingdoms.getInstance().getDataFolder().getPath() + "/kingdom_data/");
    }
}
