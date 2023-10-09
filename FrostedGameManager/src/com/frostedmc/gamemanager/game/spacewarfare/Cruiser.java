package com.frostedmc.gamemanager.game.spacewarfare;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.manager.DamageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Redraskal_2 on 3/20/2017.
 */
public class Cruiser implements Listener {

    private Player player;
    private double maxY = 80;
    private double minY = 10;
    private double speed = 1D;
    private double cannonReloadTicks = 0;

    public Cruiser(Player player) {
        this.player = player;
        GameManager.getInstance().getServer().getPluginManager().registerEvents(this,
                GameManager.getInstance());
        player.getInventory().setHelmet(new ItemStack(Material.BEACON, 1));
        player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE, 1));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
        player.getInventory().setItem(0, ItemCreator.getInstance()
                .createItem(Material.GOLD_SWORD, 1, 0, "&b&lSPEED CONTROLLER", Arrays.asList(new String[]{
                        "&eLeft-Click &7to decrease speed",
                        "&eRight-Click &7to increase speed"
                })));
        player.getInventory().setItem(1, ItemCreator.getInstance()
                .createItem(Material.DIAMOND_HOE, 1, 0, "&b&lLAZER CANNON", Arrays.asList(new String[]{
                        "&eRight-Click &7to shoot"
                })));
        this.runnable();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) return;
        if(!event.getPlayer().getName().equalsIgnoreCase(player.getName())) return;
        if(event.getItem().getType() == Material.GOLD_SWORD) {
            if(event.getAction().toString().contains("LEFT")) {
                setSpeed(speed-.3D);
            } else {
                setSpeed(speed+.3D);
                new BukkitRunnable() {
                    public void run() {
                        if(event.getPlayer().isBlocking()) {
                            if(!setSpeed(speed+.3D)) this.cancel();
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(GameManager.getInstance(), 0, 3L);
            }
        }
        if(event.getItem().getType() == Material.DIAMOND_HOE) {
            if(event.getAction().toString().contains("RIGHT")) {
                if(cannonReloadTicks == 0) {
                    player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1f, 1.5f);
                    ArmorStand laser = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    laser.setBasePlate(false);
                    laser.setSmall(true);
                    laser.setVisible(false);
                    laser.setGravity(false);
                    laser.setHelmet(new ItemStack(Material.BLAZE_ROD, 1));
                    laser.setVelocity(player.getLocation().getDirection().normalize().multiply(5));
                    new BukkitRunnable() {
                        int ticks = 0;
                        public void run() {
                            if(ticks >= (20*10)) {
                                this.cancel();
                                laser.remove();
                            } else {
                                boolean explode = false;
                                for(Entity near : laser.getNearbyEntities(1, 1, 1)) {
                                    if(near.getUniqueId() != player.getUniqueId()) {
                                        if(near instanceof Player) {
                                            if(SpectatorMode.getInstance().contains((Player) near)) continue;
                                        }
                                        explode = true;
                                        if(near instanceof Player) {
                                            DamageManager.handleDeath((Player) near, null, "Laser");
                                        } else {
                                            if(near instanceof ArmorStand) {
                                                near.remove();
                                            }
                                        }
                                    }
                                }
                                if(explode) {
                                    this.cancel();
                                    laser.remove();
                                    laser.getWorld().playSound(laser.getLocation(), Sound.EXPLODE, 1f, 1.5f);
                                    laser.getWorld().playSound(laser.getLocation(), Sound.GHAST_SCREAM, 1f, 1.5f);
                                    ParticleEffect.SMOKE_LARGE.display(1, 1, 1, 1, 15, laser.getLocation(), 54);
                                    ParticleEffect.FLAME.display(1, 1, 1, 1, 15, laser.getLocation(), 54);
                                    ParticleEffect.EXPLOSION_HUGE.display(1, 1, 1, 1, 5, laser.getLocation(), 54);
                                } else {
                                    laser.getWorld().playSound(laser.getLocation(), Sound.GHAST_CHARGE, .5f, 1.4f);
                                    laser.getWorld().playSound(laser.getLocation(), Sound.FIZZ, .6f, 1.5f);
                                    ParticleEffect.CLOUD.display(0, 0, 0, 0, 10, laser.getLocation(), 54);
                                    ParticleEffect.FLAME.display(0, 0, 0, 0, 10, laser.getLocation(), 54);
                                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 10, laser.getLocation(), 54);
                                    laser.setVelocity(laser.getVelocity().multiply(5).setY(0));
                                }
                                ticks++;
                            }
                        }
                    }.runTaskTimer(GameManager.getInstance(), 0, 1L);
                    cannonReloadTicks = 60;
                }
            }
        }
    }

    // Subtract z to go forward :D
    private void runnable() {
        new BukkitRunnable() {
            public void run() {
                if(!player.isOnline() || player.isDead()
                        || SpectatorMode.getInstance().contains(player)) {
                    this.cancel();
                } else {
                    double yVel = -.5;
                    if(player.isSneaking()) {
                        yVel = .5;
                    }
                    if(player.getLocation().getY() <= minY) {
                        yVel = .5;
                        player.playSound(player.getLocation(), Sound.ARROW_HIT, 0.5f, 0.5f);
                    }
                    if(player.getLocation().getY() >= maxY) {
                        yVel = -.5;
                        player.playSound(player.getLocation(), Sound.ARROW_HIT, 0.5f, 0.5f);
                    }
                    if(player.getInventory().getHeldItemSlot() == 0) {
                        actionBar((speed/2)*100, speed, "Light years / hour");
                    }
                    if(player.getInventory().getHeldItemSlot() == 1) {
                        if(cannonReloadTicks > 0) {
                            actionBar((100-((cannonReloadTicks/60)*100)), "Laser Cannon");
                            cannonReloadTicks--;
                        }
                    }
                    player.setVelocity(new Vector(0, yVel, -speed));
                    player.playSound(player.getLocation(), Sound.FIZZ, 0.1f, 0.6f);
                    ParticleEffect.FLAME.display(0, 0, 0, 0, 7, player.getLocation().clone()
                            .add(0, 0.3, 0), 54);
                    ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 7, player.getLocation().clone()
                            .add(0, 0.3, 0), 54);
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    private void actionBar(double percent, String type) {
        //double percent = (((Integer) points).doubleValue()/200)*100;
        String greenDots = "";
        String greyDots = "";
        for(int i=1; i<=10; i++) {
            if(percent >= (10*i)) {
                greenDots+="■";
            } else {
                greyDots+="■";
            }
        }
        ActionBar.send(player, ChatColor.translateAlternateColorCodes('&', "&8| &a"
                + greenDots + "&7" + greyDots + " &8| &a"
                + new DecimalFormat("#.#").format(percent) + "% &8| &a" + type));
    }

    private void actionBar(double percent, double value, String type) {
        //double percent = (((Integer) points).doubleValue()/200)*100;
        String greenDots = "";
        String greyDots = "";
        for(int i=1; i<=10; i++) {
            if(percent >= (10*i)) {
                greenDots+="■";
            } else {
                greyDots+="■";
            }
        }
        ActionBar.send(player, ChatColor.translateAlternateColorCodes('&', "&8| &a"
                + greenDots + "&7" + greyDots + " &8| &a"
                + new DecimalFormat("#.#").format(percent) + "% &8| &a"
                + new DecimalFormat("#.#").format(value) + " " + type));
    }

    public boolean setSpeed(double value) {
        if(value <= 1) {
            value = 1;
            return false;
        }
        if(value >= 2) {
            value = 2;
            return false;
        }
        speed = value;
        return true;
    }

    public double getSpeed() {
        return speed;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getPlayer().getName().equalsIgnoreCase(player.getName())) {
            this.despawn();
        }
    }

    public void despawn() {
        HandlerList.unregisterAll(this);
    }
}