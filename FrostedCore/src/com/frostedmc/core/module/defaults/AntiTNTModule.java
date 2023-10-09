package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 8/31/2016.
 */
public class AntiTNTModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public AntiTNTModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "AntiTNT";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent entityExplodeEvent) {
        entityExplodeEvent.setCancelled(true);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}