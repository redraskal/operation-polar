package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class LocationSpawnModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    public Location location;
    public boolean respawn = false;
    public boolean testKit = false;

    public LocationSpawnModule(JavaPlugin javaPlugin, Location location) {
        this.javaPlugin = javaPlugin;
        this.location = location;
    }

    @Override
    public String name() {
        return "LocationSpawn";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(this.location);

            if(this.testKit) {
                this.setTestKit(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(this.location);

        if(this.testKit) {
            this.setTestKit(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        if(this.respawn) {
            playerRespawnEvent.setRespawnLocation(this.location);
        }

        if(this.testKit) {
            this.setTestKit(playerRespawnEvent.getPlayer());
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent playerPickupItemEvent) {
        if(this.testKit) {
            playerPickupItemEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent playerItemConsumeEvent) {
        if(this.testKit) {
            if(playerItemConsumeEvent.getItem().getType() == Material.POTION) {
                new BukkitRunnable() {
                    public void run() {
                        if(playerItemConsumeEvent.getPlayer().isOnline()) {
                            playerItemConsumeEvent.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
                        }
                    }
                }.runTaskLater(this.javaPlugin, 5L);
            }
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    private void setTestKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        for(int i=9; i<player.getInventory().getSize(); i++) {
            ItemStack item = new ItemStack(Material.POTION, 1);
            Potion pot = new Potion(1);
            pot.setType(PotionType.INSTANT_HEAL);
            pot.setSplash(true);
            pot.apply(item);
            player.getInventory().setItem(i, item);
        }

        player.getInventory().setItem(9, new ItemStack(Material.ARROW, 1));

        player.getInventory().setItem(10, new ItemStack(Material.POTION, 1, (byte) 8226));
        player.getInventory().setItem(11, new ItemStack(Material.POTION, 1, (byte) 8226));

        player.getInventory().setItem(18, new ItemStack(Material.POTION, 1, (byte) 8226));
        player.getInventory().setItem(19, new ItemStack(Material.POTION, 1, (byte) 8226));
        player.getInventory().setItem(20, new ItemStack(Material.POTION, 1, (byte) 8226));

        player.getInventory().setItem(27, new ItemStack(Material.POTION, 1, (byte) 8226));
        player.getInventory().setItem(28, new ItemStack(Material.POTION, 1, (byte) 8226));
        player.getInventory().setItem(29, new ItemStack(Material.POTION, 1, (byte) 8226));

        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD, 1);
        diamondSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD, 1);
        fishingRod.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);

        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);

        player.getInventory().setItem(0, diamondSword);
        player.getInventory().setItem(1, fishingRod);
        player.getInventory().setItem(2, bow);
        player.getInventory().setItem(3, new ItemStack(Material.POTION, 1, (byte) 8226));

        ItemStack item = new ItemStack(Material.POTION, 1);
        Potion pot = new Potion(1);
        pot.setType(PotionType.INSTANT_HEAL);
        pot.setSplash(true);
        pot.apply(item);

        player.getInventory().setItem(4, item);
        player.getInventory().setItem(5, item);
        player.getInventory().setItem(6, item);

        player.getInventory().setItem(7, new ItemStack(Material.GOLDEN_APPLE, 64));
        player.getInventory().setItem(8, new ItemStack(Material.ENDER_PEARL, 16));
    }
}
