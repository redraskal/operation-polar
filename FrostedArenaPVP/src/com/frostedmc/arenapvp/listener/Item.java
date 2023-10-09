package com.frostedmc.arenapvp.listener;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Game;
import com.frostedmc.arenapvp.gui.RankedQueue;
import com.frostedmc.arenapvp.gui.UnrankedQueue;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.arenapvp.queue.Queue;
import com.frostedmc.core.gui.defaults.CosmeticsMainMenuGUI;
import com.frostedmc.core.gui.defaults.ProfileGUI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Item implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Game.resetInv(event.getPlayer());
        Game.lobbyInv(event.getPlayer());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!ArenaManager.getInstance().inArena((org.bukkit.entity.Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().getDisplayName() != null) {
                        String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(displayName.equalsIgnoreCase("UNRANKED QUEUE")) {
                            new UnrankedQueue(event.getPlayer());
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("RANKED QUEUE")) {
                            new RankedQueue(event.getPlayer());
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("LEAVE QUEUE")) {
                            Queue.getQueue(event.getPlayer()).remove();
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.EXPLODE, 10, 1);
                            Game.lobbyInv(event.getPlayer());
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("PROFILE")) {
                            new ProfileGUI(event.getPlayer(), ArenaPVP.getInstance());
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("LOBBY SELECTOR")) {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7This feature is coming soon."));
                            event.setCancelled(true);
                        }
                        if(displayName.equalsIgnoreCase("COSMETICS")) {
                            new CosmeticsMainMenuGUI(event.getPlayer(), ArenaPVP.getInstance());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}