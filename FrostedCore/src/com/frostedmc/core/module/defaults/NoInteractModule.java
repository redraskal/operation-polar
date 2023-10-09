package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class NoInteractModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public NoInteractModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "NoInteract";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        playerInteractEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        blockPlaceEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        blockBreakEvent.setCancelled(true);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}