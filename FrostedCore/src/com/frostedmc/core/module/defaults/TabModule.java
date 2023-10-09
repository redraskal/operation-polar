package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class TabModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public TabModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "Tab";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        for(Player player : Bukkit.getOnlinePlayers()) {
            this.runnable(player);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.runnable(event.getPlayer());
    }

    private void runnable(Player player) {
        Utils.sendCustomTab(player, "   &b&lFROSTEDMC NETWORK   ", "   &6Visit www.FrostedMC.com for the forums, store & more!   ");
    }
}