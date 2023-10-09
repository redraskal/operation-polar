package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class ForceGamemodeModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    public GameMode gameMode;

    public ForceGamemodeModule(JavaPlugin javaPlugin, GameMode gamemode) {
        this.javaPlugin = javaPlugin;
        this.gameMode = gamemode;
    }

    @Override
    public String name() {
        return "ForceGamemode";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(this.gameMode);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setGameMode(this.gameMode);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
