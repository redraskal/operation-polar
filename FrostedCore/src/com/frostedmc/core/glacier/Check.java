package com.frostedmc.core.glacier;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Redraskal_2 on 8/30/2016.
 */
public class Check implements Listener {

    private Map<UUID, List<String>> debugLogs = new HashMap<UUID, List<String>>();

    private String name;
    private boolean enabled;
    public boolean bannable = true;
    public int maxViolations = 5;
    public int countToNotify = 1;
    public long violationReset = 600000L;

    public Check(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
        if(this.enabled) {
            GlacierModule.getInstance().getPlugin().getServer().getPluginManager().registerEvents(this,
                    GlacierModule.getInstance().getPlugin());
            this.onEnable();
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean toggle) {
        if(enabled) {
            if(toggle == false) {
                enabled = false;
                HandlerList.unregisterAll(this);
                this.onDisable();
            }
        } else {
            if(toggle == true) {
                enabled = true;
                GlacierModule.getInstance().getPlugin().getServer().getPluginManager().registerEvents(this,
                        GlacierModule.getInstance().getPlugin());
                this.onEnable();
            }
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public void log(UUID player, String debug) {
        if(!debugLogs.containsKey(player)) {
            debugLogs.put(player, new ArrayList<String>());
        }
        List<String> currentLog = debugLogs.get(player);
        currentLog.add(debug);
        debugLogs.put(player, currentLog);
    }

    public void deleteLog(UUID player) {
        if(!debugLogs.containsKey(player))
            return;
        debugLogs.remove(player);
    }

    public void exportLogs(UUID player, File location) throws IOException {
        if(!debugLogs.containsKey(player))
            return;
        if(location == null)
            return;
        if(location.isDirectory())
            return;
        if(!location.exists())
            location.createNewFile();
        FileWriter fileWriter = new FileWriter(location);
        for(String line : debugLogs.get(player)) {
            fileWriter.write(line + "\n");
        }
        fileWriter.flush(); fileWriter.close();
    }
}