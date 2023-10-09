package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class NoHungerModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    public int foodLevel;

    public NoHungerModule(JavaPlugin javaPlugin, int foodLevel) {
        this.javaPlugin = javaPlugin;
        this.foodLevel = foodLevel;
    }

    @Override
    public String name() {
        return "NoHunger";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setFoodLevel(this.foodLevel);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.getPlayer().setFoodLevel(this.foodLevel);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent foodLevelChangeEvent) {
        foodLevelChangeEvent.setFoodLevel(this.foodLevel);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}