package com.frostedmc.nightfall.listener;

import com.frostedmc.nightfall.callback.FortressWorldLoadCallback;
import com.frostedmc.nightfall.manager.WorldManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class WorldManagerListener implements Listener {

    @Getter private final WorldManager worldManager;

    public WorldManagerListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if(event.getWorld().getName().equalsIgnoreCase("world")) return;
        event.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
        event.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
        event.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
        event.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
        event.getPlayer().setLevel(0);
        event.getPlayer().setExp(0);
        event.getPlayer().setFoodLevel(20);
        event.getPlayer().setExhaustion(0);

        if(event.getFrom().getName().equalsIgnoreCase("world")) {
            event.getPlayer().setHealthScale(40);
            event.getPlayer().setHealth(20);

            event.getPlayer().getInventory().setItem(8, new ItemStack(Material.WATCH, 1));
        } else {
            event.getPlayer().setHealthScale(20);
            event.getPlayer().setHealth(20);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.getWorldManager().doesPlayerHaveWorld(event.getPlayer().getUniqueId())) {
            new BukkitRunnable() {
                public void run() {
                    if(!event.getPlayer().isOnline()) return;
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bNightfall> &7Loading your fortress world..."));
                    worldManager.loadFortressWorld(event.getPlayer().getUniqueId(), new FortressWorldLoadCallback() {
                        @Override
                        public void progress(String info) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bNightfall> &7" + info));
                        }

                        @Override
                        public void done(World world) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bNightfall> &aFortress world has been loaded, teleporting you now."));
                            event.getPlayer().teleport(world.getSpawnLocation());
                        }

                        @Override
                        public void error(String message) {
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bNightfall> &c" + message));
                        }
                    });
                }
            }.runTaskLater(this.getWorldManager().getNightfall(), 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getWorldManager().unloadFortressWorld(event.getPlayer().getUniqueId());
    }
}