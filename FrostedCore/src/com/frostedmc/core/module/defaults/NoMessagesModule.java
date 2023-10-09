package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class NoMessagesModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public NoMessagesModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "NoMessages";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        asyncPlayerChatEvent.setCancelled(true);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}