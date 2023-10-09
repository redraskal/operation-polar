package com.frostedmc.gamemanager.game.rocketroyal.powerupAbility;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.NMSUtils;
import com.frostedmc.gamemanager.game.rocketroyal.PowerupAbility;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class HomingFireballAbility extends PowerupAbility implements Listener {

    @Override
    public String name() {
        return "Homing Fireballs";
    }

    @Override
    public int ticksEnabled() {
        return 20*12;
    }

    private Player targeting = null;
    private Map<Fireball, Player> targets = new HashMap<>();

    public HomingFireballAbility(Player player) {
        super(player);
    }

    @Override
    public void onActivate() {
        GameManager.getInstance().getServer().getPluginManager()
                .registerEvents(this, GameManager.getInstance());
    }

    @EventHandler
    public void onFireballSpawn(ProjectileLaunchEvent event) {
        if(targeting == null) return;
        if(!(event.getEntity() instanceof Fireball)) return;
        Fireball fireball = (Fireball) event.getEntity();
        if(fireball.getShooter() == null
                || !(fireball.getShooter() instanceof Player)) return;
        Player shooter = (Player) fireball.getShooter();
        if(!shooter.getUniqueId().equals(getPlayer().getUniqueId())) return;
        targets.put(fireball, targeting);
    }

    @EventHandler
    public void onFireballDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Fireball)) return;
        Fireball fireball = (Fireball) event.getEntity();
        if(targets.containsKey(fireball)) {
            targets.remove(fireball);
        }
    }

    @Override
    public void onTick() {
        UUID lastTarget = null;
        if(targeting != null) {
            lastTarget = targeting.getUniqueId();
        }
        if(this.getTicks() % 10 == 0) {
            this.targeting = this.getTarget();
            try {
                NMSUtils.sendActionBar(getPlayer(), "&7Look at a player to target with your Rocket Launcher.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if(targeting == null && lastTarget != null) {
            if(Bukkit.getPlayer(lastTarget) != null) {
                toggleHighlight(getPlayer(), Bukkit.getPlayer(lastTarget), false);
            }
        }
        if(targeting != null && lastTarget == null
                || targeting != null && lastTarget != null && !lastTarget.equals(targeting.getUniqueId())) {
            if(lastTarget != null && Bukkit.getPlayer(lastTarget) != null) {
                toggleHighlight(getPlayer(), Bukkit.getPlayer(lastTarget), false);
            }
            toggleHighlight(getPlayer(), targeting, true);
        }
        if(getTicks() % 5 == 0) {
            for(Map.Entry<Fireball, Player> entry : targets.entrySet()) {
                if(entry.getValue() == null) continue;
                if(!entry.getValue().isOnline()) {
                    entry.setValue(null);
                    continue;
                }
                Location fireballLocation = entry.getKey().getLocation();
                Location targetLocation = entry.getValue().getEyeLocation().clone();
                double speed = entry.getKey().getVelocity().length();
                entry.getKey().setVelocity(entry.getKey().getVelocity().add(
                        targetLocation.subtract(fireballLocation).toVector()
                                .normalize().multiply(0.9D * speed))
                        .normalize().multiply(speed));
            }
        }
    }

    @Override
    public void onDeactivate() {
        Bukkit.getOnlinePlayers().forEach(players -> {
            if(!players.getUniqueId().equals(getPlayer().getUniqueId())) {
                toggleHighlight(getPlayer(), players, false);
            }
        });
        HandlerList.unregisterAll(this);
    }

    public Player getTarget() {
        Player target = null;

        for(int blockDistance=5; blockDistance<60; blockDistance++) {
            Block block = getPlayer().getTargetBlock((Set<Material>) null, blockDistance);
            for(Entity nearby : block.getWorld().getNearbyEntities(block.getLocation(), 4, 4, 4)) {
                if(!(nearby instanceof Player)) continue;
                Player nearbyPlayer = (Player) nearby;
                if(nearbyPlayer.getUniqueId().equals(getPlayer().getUniqueId())) continue;
                if(target == null
                        || target.getLocation().distance(block.getLocation())
                        > nearbyPlayer.getLocation().distance(block.getLocation())) {
                    target = nearbyPlayer;
                }
            }
        }

        return target;
    }

    public void toggleHighlight(Player viewer, Player player, boolean highlight) {
        for(int equipmentSlot=1; equipmentSlot<5; equipmentSlot++) {
            ItemStack itemStack = new ItemStack(Material.AIR, 1);
            if(highlight) {
                switch(equipmentSlot) {
                    case 1:
                        itemStack = ItemCreator.getInstance().createArmour(
                                Material.LEATHER_BOOTS, 1,
                                Color.YELLOW, "&b");
                        break;
                    case 2:
                        itemStack = ItemCreator.getInstance().createArmour(
                                Material.LEATHER_LEGGINGS, 1,
                                Color.YELLOW, "&b");
                        break;
                    case 3:
                        itemStack = ItemCreator.getInstance().createArmour(
                                Material.LEATHER_CHESTPLATE, 1,
                                Color.YELLOW, "&b");
                        break;
                    case 4:
                        itemStack = ItemCreator.getInstance().createArmour(
                                Material.LEATHER_HELMET, 1,
                                Color.YELLOW, "&b");
                        break;
                }
            }
            PacketPlayOutEntityEquipment packetPlayOutEntityEquipment
                    = new PacketPlayOutEntityEquipment(
                            player.getEntityId(),
                    equipmentSlot, CraftItemStack.asNMSCopy(itemStack));
            ((CraftPlayer) viewer).getHandle().playerConnection
                    .sendPacket(packetPlayOutEntityEquipment);
        }
    }
}