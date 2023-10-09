package com.frostedmc.nightfall.entity;

import com.frostedmc.nightfall.utils.CustomSkullHash;
import com.frostedmc.nightfall.utils.NBTUtils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class IlluminatiEntity implements Listener {

    @Getter private final JavaPlugin javaPlugin;
    private Zombie main;
    private ArmorStand head;
    private List<ArmorStand> orbs = new ArrayList<>();

    public IlluminatiEntity(Location location, JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.getJavaPlugin().getServer().getPluginManager().registerEvents(this, this.getJavaPlugin());

        this.main = location.getWorld().spawn(location, Zombie.class);
        this.main.setBaby(true);
        this.main.setVillager(false);
        this.main.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE, 1, false, false));
        this.main.setMaxHealth(30D);
        this.main.setHealth(30D);
        try {
            NBTUtils.setInt(this.main, "Silent", 1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        this.head = location.getWorld().spawn(location, ArmorStand.class);
        this.applyArmorStandMechanics(this.head);
        this.head.setSmall(false);
        this.head.setHelmet(CustomSkullHash.ILLUMINATI_HEAD.construct());
        this.head.setCustomName(ChatColor.translateAlternateColorCodes('&',
                "&b&lIlluminati &8| &e" + this.main.getHealth() + " Health"));
        this.head.setCustomNameVisible(true);

        for(int i=0; i<4; i++) {
            ArmorStand orb = location.getWorld().spawn(location.clone(), ArmorStand.class);
            this.applyArmorStandMechanics(orb);
            orb.setSmall(true);
            orb.setHelmet(CustomSkullHash.ILLUMINATI_ORB.construct());
            this.orbs.add(orb);
        }

        new BukkitRunnable() {
            int ticks = 0;
            double yOffset = 0D;
            boolean yOffsetDir = true;
            boolean headDir = true;
            public void run() {
                if(!isAlive()) {
                    this.cancel();
                    remove();
                    return;
                }

                ticks++;

                if(ticks % 5 == 0) {
                    head.getWorld().spigot().playEffect(head.getLocation(), Effect.CLOUD,
                            0, 0, 1, 1, 1,
                            0, 5, 15);
                }

                if(ticks % 120 == 0) {
                    head.getWorld().spigot().playEffect(head.getLocation(), Effect.SMOKE,
                            0, 0, 1, 1, 1,
                            0, 5, 15);
                    head.getWorld().playSound(head.getLocation(), Sound.BLAZE_BREATH, 0.8f, 0.8f);
                    head.getWorld().playSound(head.getLocation(), Sound.MINECART_BASE, 0.8f, 0.7f);
                }

                final float radius = 0.7f;
                final float radPerSec = 1.0f;
                final float radPerTick = radPerSec / 20f;

                float offset = 0f;
                float dir = 1f;

                if(yOffsetDir) {
                    if(yOffset >= 0.5D) {
                        yOffsetDir = false;
                        yOffset-=0.006D;
                    } else {
                        yOffset+=0.006D;
                    }
                } else {
                    if(yOffset <= -0.5D) {
                        yOffsetDir = true;
                        yOffset+=0.006D;
                    } else {
                        yOffset-=0.006D;
                    }
                }

                if(headDir) {
                    head.teleport(main.getLocation().clone().add(0, 0.1D, 0));
                } else {
                    head.teleport(main.getLocation().clone().add(0, -0.1D, 0));
                }

                headDir = !headDir;

                for(ArmorStand orb : orbs) {
                    Location loc = getLocationAroundCircle(main.getLocation(), radius, radPerTick * ticks * dir + offset);
                    orb.setVelocity(new Vector(1, 0, 0));
                    orb.setHeadPose(orb.getHeadPose().add(0, 0.05f, 0.05f));
                    orb.teleport(loc.clone().add(0, 1D+yOffset, 0));
                    orb.getWorld().spigot().playEffect(orb.getLocation().clone(), Effect.SPELL,
                            0, 0, 1, 1, 1,
                            1, 1, 15);
                    if(dir > 0) {
                        dir = -1f;
                    } else {
                        dir = 1f;
                    }
                    offset+=30f;
                }
            }
        }.runTaskTimer(javaPlugin, 0, 1L);
    }

    private Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();

        Location loc = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(loc.toVector());
        loc.setDirection(difference);

        return loc;
    }

    private void applyArmorStandMechanics(ArmorStand armorStand) {
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setArms(false);
    }

    public boolean isAlive() {
        return (main != null && !main.isDead());
    }

    public boolean isEntity(Entity entity) {
        if(!this.isAlive()) return false;
        if(entity.getUniqueId() == head.getUniqueId()) return true;
        for(ArmorStand orb : orbs) {
            if(entity.getUniqueId() == orb.getUniqueId()) return true;
        }
        return false;
    }

    @EventHandler
    public void onEntityHurt(EntityDamageEvent event) {
        if(!isEntity(event.getEntity())) return;
        this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.FLAME,
                0, 0, 1, 1, 1,
                0, 15, 15);
        this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.CLOUD,
                0, 0, 1, 1, 1,
                0, 15, 15);
        this.head.getWorld().playSound(this.head.getLocation().clone(), Sound.BAT_HURT, 0.8f, 0.9f);
        this.head.getWorld().playSound(this.head.getLocation().clone(), Sound.BLAZE_HIT, 1f, 0.9f);
        if(event.getEntity().getUniqueId() != this.main.getUniqueId()
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            this.main.damage(event.getDamage());
            event.setDamage(0);
        }
        if(this.isAlive()) {
            this.head.setCustomName(ChatColor.translateAlternateColorCodes('&',
                    "&b&lIlluminati &8| &e"
                            + new DecimalFormat("#.#").format(this.main.getHealth()) + " Health"));
            this.head.setCustomNameVisible(true);
        }
    }

    @EventHandler
    public void onEntityHurt(EntityDamageByEntityEvent event) {
        if(!isEntity(event.getEntity())) return;
        if(event.getEntity().getUniqueId() != this.main.getUniqueId()
                && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            this.main.damage(event.getDamage(), event.getDamager());
            event.setDamage(0);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(this.main == null || this.main.getUniqueId() != event.getEntity().getUniqueId()) return;
        event.setDroppedExp(0);
        event.getDrops().clear();
        event.getDrops().add(new ItemStack(Material.REDSTONE, 3));
        event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 2));
    }

    public void remove() {
        HandlerList.unregisterAll(this);

        if(this.head != null) {
            this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.FLAME,
                    0, 0, 1, 1, 1,
                    0, 15, 15);
            this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.CLOUD,
                    0, 0, 1, 1, 1,
                    0, 15, 15);
            this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.EXPLOSION_LARGE,
                    0, 0, 1, 1, 1,
                    0, 3, 15);
            this.head.getWorld().spigot().playEffect(this.head.getLocation().clone(), Effect.EXPLOSION,
                    0, 0, 1, 1, 1,
                    0, 3, 15);
            this.head.getWorld().playSound(this.head.getLocation().clone(), Sound.EXPLODE, 1f, 1.3f);
            this.head.getWorld().playSound(this.head.getLocation().clone(), Sound.BLAZE_DEATH, 0.8f, 0.9f);
        }

        if(this.main != null) this.main.remove();
        this.main = null;
        if(this.head != null) this.head.remove();
        this.head = null;
        this.orbs.forEach(orb -> orb.remove());
        this.orbs.clear();
    }
}