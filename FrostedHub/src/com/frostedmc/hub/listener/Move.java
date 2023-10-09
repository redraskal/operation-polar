package com.frostedmc.hub.listener;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.hub.Hub;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class Move implements Listener {

    private Map<UUID, Long> lastMsg = new HashMap<UUID, Long>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo().getY() >= 98) {
            event.getPlayer().setVelocity(new Vector(0, -1, 0));
        }
    }

    @EventHandler
    public void onEntityManipulate(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if(event.isSneaking()) {
            if(event.getPlayer().getInventory().getBoots() != null
                    && event.getPlayer().getInventory().getBoots().getType() != Material.AIR) {
                event.setCancelled(true);
                event.getPlayer().setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(2).setY(0));
                new BukkitRunnable() {
                    int ticks = 0;
                    public void run() {
                        Vector vectorLeft = getLeftVector(event.getPlayer().getLocation()).normalize().multiply(0.15);
                        Vector vectorRight = getRightVector(event.getPlayer().getLocation()).normalize().multiply(0.15);
                        Location locationLeft = event.getPlayer().getLocation().add(vectorLeft);
                        Location locationRight = event.getPlayer().getLocation().add(vectorRight);
                        locationLeft.setY(event.getPlayer().getLocation().getY());
                        locationRight.setY(event.getPlayer().getLocation().getY());

                        ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.ICE, (byte) 0), 0, 0, 0, 0f, 0, locationLeft, 32);
                        ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.ICE, (byte) 0), 0, 0, 0, 0f, 0, locationRight, 32);

                        if(ticks >= 20*2) {
                            this.cancel();
                        }

                        ticks++;
                    }
                }.runTaskTimer(Hub.getInstance(), 0, 1L);
            }
        }
    }

    public static Vector getLeftVector(Location loc) {
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));
        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public static Vector getRightVector(Location loc) {
        final float newX = (float) (loc.getX() + (-1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (-1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));
        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveMonitor(PlayerMoveEvent event) {
        if(event.getTo().getY() > 76) {
            if(event.getPlayer().getInventory().getBoots() != null
                    && event.getPlayer().getInventory().getBoots().getType() != Material.AIR) {
                event.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
            }
            return;
        }
        for(Block block : Utils.getBlocksInRadius(event.getTo(), 5, false)) {
            if(block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE) {
                if(event.getPlayer().getInventory().getBoots() == null
                        || event.getPlayer().getInventory().getBoots().getType() == Material.AIR) {
                    if(Core.getInstance().getAccountManager().parseDetails(event.getPlayer().getUniqueId()).getRank()
                            != Rank.PLAYER) {
                        event.getPlayer().getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                        boolean msg = true;
                        if(lastMsg.containsKey(event.getPlayer().getUniqueId())) {
                            if((System.currentTimeMillis()-lastMsg.get(event.getPlayer().getUniqueId()))
                                    <= 10000) {
                                msg = false;
                            }
                        }
                        if(msg) {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PIANO, 10, 1);
                            ChatUtils.sendBlockMessage("Ice Skating", new String[]{
                                    "&bTurn on your festive spirit with",
                                    "&bIce Skating!",
                                    "&a&lHold Shift to skate"
                            }, event.getPlayer());
                            lastMsg.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                        }
                    } else {
                        boolean msg = true;
                        if(lastMsg.containsKey(event.getPlayer().getUniqueId())) {
                            if((System.currentTimeMillis()-lastMsg.get(event.getPlayer().getUniqueId()))
                                    <= 10000) {
                                msg = false;
                            }
                        }
                        if(msg) {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PIANO, 10, 1);
                            ChatUtils.sendBlockMessage("Ice Skating", new String[]{
                                    "&bTurn on your festive spirit with",
                                    "&bIce Skating!",
                                    "&c&lPurchase a rank to skate!"
                            }, event.getPlayer());
                            lastMsg.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                        }
                    }
                }
                return;
            }
        }
        if(event.getPlayer().getInventory().getBoots() != null
                && event.getPlayer().getInventory().getBoots().getType() != Material.AIR) {
            event.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
        }
    }
}