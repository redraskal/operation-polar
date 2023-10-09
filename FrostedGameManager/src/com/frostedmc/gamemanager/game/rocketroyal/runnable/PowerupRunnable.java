package com.frostedmc.gamemanager.game.rocketroyal.runnable;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.MathUtils;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.game.rocketroyal.Powerup;
import com.frostedmc.gamemanager.game.rocketroyal.RocketRoyal;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class PowerupRunnable extends BukkitRunnable {

    private final RocketRoyal rocketRoyal;
    private int secondsUntilNextPowerup = 10+new Random().nextInt(30);

    public PowerupRunnable(RocketRoyal rocketRoyal) {
        this.rocketRoyal = rocketRoyal;
        this.runTaskTimer(GameManager.getInstance(), 0, 10);
    }

    @Override
    public void run() {
        if(GameManager.getInstance().getCurrentGame() == null
                || GameManager.getInstance().getCurrentGame().getGameUUID() != rocketRoyal.getGameUUID()) {
            this.cancel();
            return;
        }
        Powerup activePowerup = null;
        for(Powerup powerup : rocketRoyal.getPowerups()) {
            if(powerup.isActive()) {
                activePowerup = powerup;
                break;
            }
        }
        if(activePowerup == null && secondsUntilNextPowerup <= 0) {
            Powerup powerup = rocketRoyal.getPowerups()
                    .get(new Random().nextInt(rocketRoyal.getPowerups().size()));
            powerup.setActive(true);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7A &a&lPOWERUP&7 has spawned... go find it!"));
            Title title = new Title("&aA POWERUP has spawned...",
                    "&2Go find it!", 0, 1, 1);
            title.broadcast();
            Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.NOTE_PIANO, 0.9f, 1f));
        }
        if(activePowerup != null) {
            Location centerLocation = activePowerup.getGlassBlock()
                    .getRelative(BlockFace.UP).getLocation().clone();
            centerLocation.getWorld().spigot().playEffect(centerLocation, Effect.HAPPY_VILLAGER,
                    0, 0,
                    2, 2, 2,
                    0, 15, 30);
            centerLocation.getWorld().spigot().playEffect(centerLocation, Effect.FIREWORKS_SPARK,
                    0, 0,
                    2, 2, 2,
                    0, 15, 30);
            Player closestPlayer = null;
            for(Entity nearby : centerLocation.getWorld().getNearbyEntities(centerLocation, 2.5, 2.5, 2.5)) {
                if(!(nearby instanceof Player)) continue;
                Player player = (Player) nearby;
                if(SpectatorMode.getInstance().contains(player)) continue;
                if(closestPlayer == null
                        || closestPlayer.getLocation().distance(centerLocation)
                            > player.getLocation().distance(centerLocation)) {
                    closestPlayer = player;
                }
            }
            if(closestPlayer != null) {
                activePowerup.setActive(false);
                centerLocation.getWorld().spigot().playEffect(centerLocation, Effect.EXPLOSION_LARGE,
                        0, 0,
                        2, 2, 2,
                        0, 6, 30);
                centerLocation.getWorld().spigot().playEffect(centerLocation, Effect.LARGE_SMOKE,
                        0, 0,
                        2, 2, 2,
                        0, 15, 30);
                centerLocation.getWorld().spigot().playEffect(centerLocation, Effect.FLAME,
                        0, 0,
                        2, 2, 2,
                        0, 15, 30);
                centerLocation.getWorld().playSound(centerLocation, Sound.EXPLODE, 1f, 1f);
                centerLocation.getWorld().playSound(centerLocation, Sound.LEVEL_UP, 1f, 1f);
                for(Entity nearby : centerLocation.getWorld().getNearbyEntities(centerLocation, 3, 3, 3)) {
                    if(nearby instanceof Player && SpectatorMode.getInstance().contains((Player) nearby)) continue;
                    double dX = centerLocation.getX() - nearby.getLocation().getX();
                    double dY = centerLocation.getY() - nearby.getLocation().getY();
                    double dZ = centerLocation.getZ() - nearby.getLocation().getZ();
                    double yaw = Math.atan2(dZ, dX);
                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                    double X = Math.sin(pitch) * Math.cos(yaw);
                    double Y = Math.sin(pitch) * Math.sin(yaw);
                    double Z = Math.cos(pitch);
                    Vector vector = new Vector(X, Z, Y);
                    MathUtils.applyVelocity(nearby, vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + closestPlayer.getName() + " &7has claimed a &a&lPOWERUP&7!"));
                secondsUntilNextPowerup = 20+new Random().nextInt(60);
                new PowerupGiveRunnable(rocketRoyal, closestPlayer);
            }
        }
        secondsUntilNextPowerup--;
    }
}
