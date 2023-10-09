package com.frostedmc.frostedgames.listener;

import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.game.chest.ChestFiller;
import com.frostedmc.frostedgames.game.chest.ChestType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Redraskal_2 on 1/22/2017.
 */
public class Item implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(InternalGameSettings.status == Status.WAITING
                || InternalGameSettings.status == Status.STARTING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if(InternalGameSettings.status == Status.WAITING
                || InternalGameSettings.status == Status.STARTING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(InternalGameSettings.status == Status.WAITING
                || InternalGameSettings.status == Status.STARTING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().getDisplayName() != null) {
                        String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(displayName.equalsIgnoreCase("TEAM SELECTOR")) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7This feature is coming soon."));
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("LEAVE GAME")) {
                            MessageChannel.getInstance().Switch(event.getPlayer(), event.getPlayer().getName(), "FGL-");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        if(event.getClickedBlock() != null) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(event.getClickedBlock().getType() == Material.CHEST) {
                    Chest chest = (Chest) event.getClickedBlock().getState();
                    if(event.getClickedBlock().getBiome() != Biome.HELL) {
                        ChestFiller.fillByType(chest.getInventory(),
                                ChestType.TIER_1);
                        event.getClickedBlock().setBiome(Biome.HELL);
                    }
                }
            }
        }
    }
}