package com.frostedmc.gamemanager.manager;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.event.CustomDeathEvent;
import com.frostedmc.gamemanager.game.borderbusters.BorderBusters;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public class DamageManager {

    private static DamageManager instance;
    public static DamageManager getInstance() { return instance; }
    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) return false;
        instance = new DamageManager(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;

    private DamageManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public static void handleDeath(Player entity, Entity damager, String cause) {
        if(SpectatorMode.getInstance().contains(entity)) return;
        if(GameManager.getInstance().getCurrentGame().gameFlags.dropItemsOnDeath) {
            for(ItemStack item : entity.getInventory().getContents()) {
                if(item != null && item.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                }
            }
        }
        if(GameManager.getInstance().getCurrentGame().gameFlags.dropArmorOnDeath) {
            for(ItemStack item : entity.getInventory().getArmorContents()) {
                if(item != null && item.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                }
            }
        }
        if(damager != null) {
            boolean defaultM = true;
            if(cause.equalsIgnoreCase("Projectile")) {
                if(damager instanceof Projectile) {
                    Projectile projectile = (Projectile) damager;
                    if(projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                        if(projectile instanceof Arrow) {
                            double projectile_height = projectile.getLocation().getY();
                            double player_height = entity.getLocation().getY()+1.35;
                            if(projectile_height > player_height) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was shot in the head by &e"
                                        + ((Player) projectile.getShooter()).getName() + " &7with &e"
                                        + formatElement(projectile.getType().toString().toLowerCase()) + " &7from &e"
                                        + new DecimalFormat("#.#").format(entity.getLocation().distance(((Player) projectile.getShooter()).getLocation()))
                                        + " &7blocks away."));
                            } else {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was shot by &e"
                                        + ((Player) projectile.getShooter()).getName() + " &7with &e"
                                        + formatElement(projectile.getType().toString().toLowerCase()) + " &7from &e"
                                        + new DecimalFormat("#.#").format(entity.getLocation().distance(((Player) projectile.getShooter()).getLocation()))
                                        + " &7blocks away."));
                            }
                        } else {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                                    + ((Player) projectile.getShooter()).getName() + " &7with &e"
                                    + formatElement(projectile.getType().toString().toLowerCase()) + " &7from &e"
                                    + new DecimalFormat("#.#").format(entity.getLocation().distance(((Player) projectile.getShooter()).getLocation()))
                                    + " &7blocks away."));
                        }
                        defaultM = false;
                    }
                }
            }
            if(damager instanceof Player) {
                String item = "Fist";
                if(((Player) damager).getItemInHand() != null) {
                    if(((Player) damager).getItemInHand().getType() != Material.AIR) {
                        item = ((Player) damager).getItemInHand().getType().toString().toLowerCase();
                        item = formatElement(item);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                        + ((Player) damager).getName() + " &7with &e" + item + "&7."));
                defaultM = false;
            }
            if(defaultM) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                        + damager.getName() + " &7with &e" + cause + "&7."));
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e" + cause + "&7."));
        }
        if(damager != null) {
            if(damager instanceof Player) {
                //TODO: Add kill statistic
            }
        }
        //TODO: Add death statistic
        entity.getWorld().strikeLightningEffect(entity.getLocation());
        entity.getWorld().playSound(entity.getLocation(), Sound.FIREWORK_LARGE_BLAST, 10, 5);
        SpectatorMode.getInstance().add(entity);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 3), true);
        CustomDeathEvent customDeathEvent = new CustomDeathEvent(entity, damager, cause);
        Bukkit.getServer().getPluginManager().callEvent(customDeathEvent);
    }

    public static void shootFireworks(Location center) {
        for(int i=0; i<24; i++) {
            Location randomLocation = center.clone();
            randomLocation.setY(randomLocation.getY()+10+new Random().nextInt(3));
            if(new Random().nextDouble() >= .5) {
                randomLocation.setX(randomLocation.getX()-9-new Random().nextInt(8));
            } else {
                randomLocation.setX(randomLocation.getX()+9+new Random().nextInt(8));
            }
            if(new Random().nextDouble() <= .5) {
                randomLocation.setZ(randomLocation.getZ()-9-new Random().nextInt(8));
            } else {
                randomLocation.setZ(randomLocation.getZ()+9+new Random().nextInt(8));
            }
            Utils.shootRandomFirework(randomLocation);
        }
    }

    public static String formatElement(String element) {
        String temp = element.toString().toLowerCase().replace("_", " ");
        temp = WordUtils.capitalizeFully(temp);
        return temp;
    }

    public static String formatCause(EntityDamageEvent.DamageCause damageCause) {
        String temp = damageCause.toString().toLowerCase().replace("_", " ");
        temp = WordUtils.capitalizeFully(temp);
        if(temp.equalsIgnoreCase("Suffocation")) {
            if(GameManager.getInstance().getCurrentGame() != null) {
                if(GameManager.getInstance().getCurrentGame() instanceof BorderBusters) {
                    return "World Border";
                }
            }
        }
        return temp;
    }
}