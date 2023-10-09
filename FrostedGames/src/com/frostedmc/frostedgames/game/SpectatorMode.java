package com.frostedmc.frostedgames.game;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.frostedgames.gui.SpectateGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 1/22/2017.
 */
public class SpectatorMode implements Listener {

    private static SpectatorMode instance;
    public static SpectatorMode getInstance() { return instance; }
    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null)
            return false;
        instance = new SpectatorMode(javaPlugin);
        return true;
    }

    private List<Player> spectators = new ArrayList<Player>();

    private SpectatorMode(JavaPlugin javaPlugin) {
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    public boolean add(Player player) {
        if(spectators.contains(player))
            return false;
        hidePlayer(player);
        spectators.add(player);
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.getInventory().setItem(3,
                ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&b&lPLAYER TRACKER"));
        player.getInventory().setItem(5,
                ItemCreator.getInstance().createItem(Material.TORCH, 1, 0, "&e&lSPONSOR TRIBUTE"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You are now in &eSpectator Mode&7."));
        return true;
    }

    public boolean remove(Player player) {
        if(!spectators.contains(player))
            return false;
        showPlayer(player);
        spectators.remove(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You have left &eSpectator Mode&7."));
        return true;
    }

    public boolean contains(Player player) {
        return spectators.contains(player);
    }

    private void hidePlayer(Player player) {
        for(Player shown : Bukkit.getOnlinePlayers()) {
            shown.hidePlayer(player);
        }
    }

    private void showPlayer(Player player) {
        for(Player hidden : Bukkit.getOnlinePlayers()) {
            hidden.showPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(Player spectator : spectators) {
            event.getPlayer().hidePlayer(spectator);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getEntity();
        if(spectators.contains(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(spectators.contains(event.getPlayer())) {
            spectators.remove(event.getPlayer());
            showPlayer(event.getPlayer());
        } else {
            for(Player spectator : spectators) {
                event.getPlayer().showPlayer(spectator);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
        if(spectators.contains(event.getPlayer())) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().hasDisplayName()) {
                        String d = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(d.equalsIgnoreCase("player tracker")) {
                            new SpectateGUI(event.getPlayer(), 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getDamager();
        if(spectators.contains(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(spectators.contains(event.getWhoClicked()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(spectators.contains(event.getPlayer()))
            event.setCancelled(true);
    }
}