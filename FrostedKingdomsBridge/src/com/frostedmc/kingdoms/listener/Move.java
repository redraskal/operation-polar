package com.frostedmc.kingdoms.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class Move implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo().getWorld().getName().equalsIgnoreCase("spawn")) {
            if(event.getTo().getY() <= 10) {
                event.setTo(new Location(Bukkit.getWorld("spawn"), 1.5, 115.0, 2.5, 179.8f, 0.8f));
            }
        }
    }
}