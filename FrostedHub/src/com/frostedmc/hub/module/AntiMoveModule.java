package com.frostedmc.hub.module;

import com.frostedmc.core.module.Module;
import com.frostedmc.hub.Hub;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 2/16/2017.
 */
public class AntiMoveModule extends Module implements Listener {

    @Override
    public String name() {
        return "Anti Move";
    }

    public static List<Player> disallow = new ArrayList<Player>();

    @Override
    public void onEnable() {
        disallow.clear();
        Hub.getInstance().getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(disallow.contains(event.getPlayer())) {
            if(event.getTo().getX() != event.getFrom().getX()
                     || event.getTo().getZ() != event.getFrom().getZ()) {
                Location temp = event.getFrom();
                temp.setY(event.getTo().getY());
                temp.setYaw(event.getTo().getYaw());
                temp.setPitch(event.getTo().getPitch());
                event.setTo(temp);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(disallow.contains(event.getPlayer())) disallow.remove(event.getPlayer());
    }

    @Override
    public void onDisable() {
        disallow.clear();
        HandlerList.unregisterAll(this);
    }
}