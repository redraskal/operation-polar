package com.frostedmc.fabrication.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Redraskal_2 on 10/30/2016.
 */
public class Items implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent playerDropItemEvent) {
        playerDropItemEvent.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent playerPickupItemEvent) {
        playerPickupItemEvent.setCancelled(true);
    }
}