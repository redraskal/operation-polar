package com.frostedmc.gamemanager.listener;

import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Item implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(GameManager.getInstance().gameStatus == GameStatus.WAITING
                || GameManager.getInstance().gameStatus == GameStatus.STARTING)
            event.setCancelled(true);
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.allowInventoryClick) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if(GameManager.getInstance().gameStatus == GameStatus.WAITING
                || GameManager.getInstance().gameStatus == GameStatus.STARTING)
            event.setCancelled(true);
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.allowItemDrop) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(GameManager.getInstance().gameStatus == GameStatus.WAITING
                || GameManager.getInstance().gameStatus == GameStatus.STARTING)
            event.setCancelled(true);
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.allowItemPickup) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().getDisplayName() != null) {
                        String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(displayName.equalsIgnoreCase("LEAVE GAME")) {
                            MessageChannel.getInstance().Switch(event.getPlayer(), event.getPlayer().getName(), "Hub-");
                            event.setCancelled(true);
                        } else {
                            if(GameManager.getInstance().getCurrentGame() != null) {
                                GameManager.getInstance().getCurrentGame().onItemClick(event.getPlayer(), event.getItem(),
                                        event.getPlayer().getInventory().getHeldItemSlot());
                            }
                        }
                    }
                }
            }
        }
    }
}