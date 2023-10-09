package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class HidePlayersModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public HidePlayersModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "HidePlayers";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            for(Player others : Bukkit.getOnlinePlayers()) {
                others.hidePlayer(player);
                player.hidePlayer(others);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(Player others : Bukkit.getOnlinePlayers()) {
            others.hidePlayer(event.getPlayer());
            event.getPlayer().hidePlayer(others);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
