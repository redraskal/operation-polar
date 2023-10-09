package com.frostedmc.gamemanager.mechanic;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.NMSUtils;
import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.event.CustomDeathEvent;
import com.frostedmc.gamemanager.event.GameEndEvent;
import com.frostedmc.gamemanager.event.GameMechanicRespawnEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class RespawnMechanic implements Listener {

    @Getter @Setter private int seconds;
    @Getter @Setter private List<CustomLocation> spawnpoints;
    @Getter @Setter private boolean killerView = true;
    @Getter @Setter private GameMode respawnGameMode = GameMode.ADVENTURE;

    public RespawnMechanic(int seconds, List<CustomLocation> spawnpoints) {
        this.seconds = seconds;
        this.spawnpoints = spawnpoints;
        GameManager.getInstance().getServer().getPluginManager()
                .registerEvents(this, GameManager.getInstance());
    }

    @EventHandler
    public void onPlayerDeath(CustomDeathEvent event) {
        Player player = event.getEntity();
        Title title = new Title("You have died!", "Respawning in " + seconds + " seconds...");
        title.setTitleColor(ChatColor.RED);
        title.setSubtitleColor(ChatColor.YELLOW);
        title.send(player);
        if(this.killerView && event.getDamager() != null && !event.getDamager().isDead()) {
            player.teleport(event.getDamager().getLocation());
            player.setGameMode(GameMode.SPECTATOR);
            try {
                NMSUtils.sendCameraPacket(player, event.getDamager().getEntityId(), GameManager.getInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            try {
                NMSUtils.sendActionBar(player, "&7You are now spectating &e"
                        + event.getDamager().getName() + "&7.");
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
        player.playSound(event.getEntity().getLocation(), Sound.BLAZE_DEATH, 5, 1);
        new BukkitRunnable() {
            public void run() {
                if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
                    if(player.isOnline()) {
                        player.teleport(spawnpoints.get(new Random().nextInt(spawnpoints.size()))
                                .convert(player.getWorld()));
                        SpectatorMode.getInstance().remove(player);
                        GameManager.getInstance().getServer().getPluginManager()
                                .callEvent(new GameMechanicRespawnEvent(player));
                    }
                }
                if(player.isOnline()) {
                    try {
                        player.setGameMode(respawnGameMode);
                        NMSUtils.sendCameraPacket(player, player.getEntityId(), GameManager.getInstance());
                        player.teleport(player.getLocation().add(0, 1D, 0));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLater(GameManager.getInstance(), 20*seconds);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        HandlerList.unregisterAll(this);
    }
}