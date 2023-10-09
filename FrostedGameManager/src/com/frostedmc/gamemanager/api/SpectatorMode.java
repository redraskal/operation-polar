package com.frostedmc.gamemanager.api;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.event.SpectatorModeEnterEvent;
import com.frostedmc.gamemanager.event.SpectatorModeExitEvent;
import com.frostedmc.gamemanager.gui.SpectateGUI;
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
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

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

    public List<Player> get() {
        return this.spectators;
    }

    public boolean add(Player player) {
        if(spectators.contains(player))
            return false;
        hidePlayer(player);
        spectators.add(player);
        GameManager.getInstance().getServer().getPluginManager().callEvent(new SpectatorModeEnterEvent(player));
        player.spigot().setCollidesWithEntities(false);
        player.setLevel(0);
        player.setExp(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.CREATIVE);
        player.setFlying(true);
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.getInventory().setItem(0,
                ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&b&lPLAYER TRACKER"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You are now in &eSpectator Mode&7."));
        return true;
    }

    public boolean remove(Player player) {
        if(!spectators.contains(player))
            return false;
        showPlayer(player);
        spectators.remove(player);
        GameManager.getInstance().getServer().getPluginManager().callEvent(new SpectatorModeExitEvent(player));
        player.spigot().setCollidesWithEntities(true);
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
            if(shown.getUniqueId() == player.getUniqueId()) continue;
            shown.hidePlayer(player);
        }
    }

    private void showPlayer(Player player) {
        for(Player hidden : Bukkit.getOnlinePlayers()) {
            if(hidden.getUniqueId() == player.getUniqueId()) continue;
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