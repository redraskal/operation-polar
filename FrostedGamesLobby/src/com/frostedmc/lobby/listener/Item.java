package com.frostedmc.lobby.listener;

import com.frostedmc.core.gui.defaults.StatisticsGUI;
import com.frostedmc.lobby.FrostedLobby;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class Item implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().getDisplayName() != null) {
                        String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(displayName.equalsIgnoreCase("PLAYER STATISTICS")) {
                            new StatisticsGUI(event.getPlayer(), FrostedLobby.getInstance());
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("LOBBY SELECTOR")) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7This feature is coming soon."));
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("AURA SELECTOR")) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7This feature is coming soon."));
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
