package com.frostedmc.arenapvp.api;

import com.frostedmc.arenapvp.ArenaPVP;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.UUID;

public class Map {

    private String name;
    private World instance;

    public Map(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public World getInstance() {
        return this.instance;
    }

    public boolean load() throws Exception {
        if(instance != null) return false;
        File mapFolder = new File("/home/network/maps/"
                + this.name.toLowerCase() + "/");
        String mapName = "map_" + UUID.randomUUID().toString();
        Bukkit.getServer().getLogger().info("[/] Copying map from " + mapFolder.getAbsoluteFile().getPath());
        FileUtils.copyDirectory(mapFolder.getAbsoluteFile(),
                new File(Bukkit.getWorldContainer().getPath() + "/" + mapName + "/"));

        Bukkit.getServer().getLogger().info("[/] Loading " + mapName + ".");
        this.instance = Bukkit.getServer().createWorld(new WorldCreator(mapName));
        instance.setGameRuleValue("doFireTick", "false");
        instance.setGameRuleValue("doMobSpawning", "false");
        instance.setGameRuleValue("randomTickSpeed", "0");
        instance.setGameRuleValue("doDaylightCycle", "false");
        return true;
    }

    public boolean unload() throws Exception {
        if(instance == null) return false;
        Bukkit.getServer().getLogger().info("[/] Unloading " + instance.getName() + ".");
        final File temp = instance.getWorldFolder();
        final String name = instance.getName();
        this.instance.setAutoSave(false);
        this.instance = null;
        Utils.UnloadWorld(ArenaPVP.getInstance(), Bukkit.getWorld(name), false);
        Utils.ClearWorldReferences(name);
        File sessionLock = new File(temp, "session.lock");
        sessionLock.setReadable(true);
        sessionLock.setWritable(true);
        sessionLock.setExecutable(true);
        sessionLock.delete();
        FileUtils.deleteDirectory(temp);
        Bukkit.getServer().getLogger().info("[/] Unloaded " + name + ".");
        return true;
    }
}