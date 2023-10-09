package com.frostedmc.gamemanager.api;

import com.frostedmc.gamemanager.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public class Map {

    private String name;
    private CustomLocation spectatorLocation;
    private World instance;

    public Map(String name, CustomLocation spectatorLocation) {
        this.name = name;
        this.spectatorLocation = spectatorLocation;
    }

    public String getName() {
        return this.name;
    }

    public CustomLocation getSpectatorLocation() {
        return this.spectatorLocation;
    }

    public World getInstance() {
        return this.instance;
    }

    public boolean load() {
        if(instance != null) return false;
        File mapFolder = new File("/home/maps/"
                + this.name.toLowerCase().replace(" ", "_") + "/");
        String mapName = "map_" + UUID.randomUUID().toString();
        Bukkit.getServer().getLogger().info("[/] Copying map from " + mapFolder.getAbsoluteFile().getPath());
        //FileUtils.copyDirectory(mapFolder.getAbsoluteFile(),
                //new File(Bukkit.getWorldContainer().getPath() + "/" + mapName + "/"));
        try {
            FileUtils.quickCopy(mapFolder, new File(Bukkit.getWorldContainer().getPath() + "/" + mapName + "/"));

            Bukkit.getServer().getLogger().info("[/] Loading " + mapName + ".");
            this.instance = Bukkit.getServer().createWorld(new WorldCreator(mapName));
            instance.setGameRuleValue("doFireTick", "false");
            instance.setGameRuleValue("doMobSpawning", "false");
            instance.setGameRuleValue("randomTickSpeed", "0");
            instance.setGameRuleValue("doDaylightCycle", "false");

            return true;
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("[!] Failed to load " + name + ".");
            e.printStackTrace();
            return false;
        }
    }

    public boolean unload() {
        if(instance == null) return false;
        Bukkit.getServer().getLogger().info("[/] Unloading " + instance.getName() + ".");
        final File temp = instance.getWorldFolder();
        final String name = instance.getName();

        instance.setAutoSave(false);
        instance.getEntities().forEach(entity -> entity.remove());
        for(Chunk chunk : instance.getLoadedChunks()) chunk.unload(true);
        Bukkit.getServer().unloadWorld(instance, true);
        this.instance = null;
        //Utils.UnloadWorld(GameManager.getInstance(), Bukkit.getWorld(name), false);
        //Utils.ClearWorldReferences(name);

        File sessionLock = new File(temp, "session.lock");
        if(sessionLock.exists()) {
            sessionLock.setReadable(true);
            sessionLock.setWritable(true);
            sessionLock.setExecutable(true);
            sessionLock.delete();
        }

        try {
            FileUtils.quickDelete(temp);
            Bukkit.getServer().getLogger().info("[/] Unloaded " + name + ".");
            return true;
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("[!] Failed to unload " + name + ".");
            e.printStackTrace();
            return false;
        }
    }
}