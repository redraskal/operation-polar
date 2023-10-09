package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class NoStreamModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public NoStreamModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "NoStream";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        playerQuitEvent.setQuitMessage(null);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}