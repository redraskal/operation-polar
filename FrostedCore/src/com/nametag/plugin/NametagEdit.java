package com.nametag.plugin;

import com.nametag.plugin.api.INametagApi;
import com.nametag.plugin.api.NametagAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class NametagEdit {

    private static INametagApi api;

    public NametagHandler handler;
    public NametagManager manager;

    public void onEnable(JavaPlugin javaPlugin) {
        handler = new NametagHandler(this, javaPlugin);
        manager = new NametagManager();

        if (api == null) {
            api = new NametagAPI();
            NametagAPI.manager = manager;
        }
    }

    public void onDisable() {
        manager.reset();
    }

    public static INametagApi getApi() {
        return api;
    }
    
    public NametagManager getManager() {
    	return manager;
    }
}