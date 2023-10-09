package com.frostedmc.transactions.modules;

import com.frostedmc.core.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class KickModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public KickModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "Kick";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        event.setKickMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot join this server."));
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
    }
}