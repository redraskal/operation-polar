package com.frostedmc.gamemanager.mechanic;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.Cuboid;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.event.GameEndEvent;
import com.frostedmc.gamemanager.manager.DamageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class MapBoundaryMechanic implements Listener {

    @Getter @Setter private int secondsBeforeKill;
    @Getter @Setter private Cuboid mapBoundary;

    private List<Player> outsideBoundary = new ArrayList<>();

    public MapBoundaryMechanic(int secondsBeforeKill, Cuboid mapBoundary) {
        this.secondsBeforeKill = secondsBeforeKill;
        this.mapBoundary = mapBoundary;
        GameManager.getInstance().getServer().getPluginManager()
                .registerEvents(this, GameManager.getInstance());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getTo().getWorld().getName()
                .equals(getMapBoundary().getWorld().getName())) return;
        boolean insideBoundary = mapBoundary.hasEntityInside(event.getPlayer());
        if(insideBoundary) {
            if(outsideBoundary.contains(event.getPlayer())) {
                outsideBoundary.remove(event.getPlayer());
            }
        } else {
            if(!outsideBoundary.contains(event.getPlayer())) {
                outsideBoundary.add(event.getPlayer());
                new BukkitRunnable() {
                    int halfSeconds = 0;
                    public void run() {
                        if(!event.getPlayer().isOnline()
                                || !outsideBoundary.contains(event.getPlayer())
                                || SpectatorMode.getInstance().contains(event.getPlayer())) {
                            this.cancel();
                            if(outsideBoundary.contains(event.getPlayer())) {
                                outsideBoundary.remove(event.getPlayer());
                            }
                            return;
                        }
                        if(halfSeconds >= (secondsBeforeKill*2)) {
                            this.cancel();
                            outsideBoundary.remove(event.getPlayer());
                            DamageManager.handleDeath(
                                    event.getPlayer(),
                                    null,
                                    "Map Boundary");
                            return;
                        } else {
                            Title title = new Title("&cWarning",
                                    "&4You will be killed if you stay outside the Map Boundary.",
                                    0, 1, 0);
                            title.send(event.getPlayer());
                            event.getPlayer().playSound(event.getPlayer().getLocation(),
                                    Sound.NOTE_BASS, 1f, 1.1f);
                        }
                        halfSeconds++;
                    }
                }.runTaskTimer(GameManager.getInstance(), 0, 10L);
            }
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        HandlerList.unregisterAll(this);
    }
}