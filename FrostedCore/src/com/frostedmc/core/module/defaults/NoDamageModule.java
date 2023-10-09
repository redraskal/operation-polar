package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class NoDamageModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    public int hearts;

    public NoDamageModule(JavaPlugin javaPlugin, int hearts) {
        this.javaPlugin = javaPlugin;
        this.hearts = hearts;
    }

    @Override
    public String name() {
        return "NoDamage";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setHealthScale(hearts);
            player.setHealth(hearts);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.getPlayer().setHealthScale(hearts);
        playerJoinEvent.getPlayer().setHealth(hearts);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        entityDamageEvent.setCancelled(true);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}