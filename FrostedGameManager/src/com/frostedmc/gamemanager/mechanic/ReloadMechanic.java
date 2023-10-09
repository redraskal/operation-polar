package com.frostedmc.gamemanager.mechanic;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.event.GameEndEvent;
import com.frostedmc.gamemanager.event.SpectatorModeEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ReloadMechanic implements Listener {

    private List<Player> reloading = new ArrayList<>();

    public ReloadMechanic(boolean flickerWhenAvailable) {
        GameManager.getInstance().getServer().getPluginManager()
                .registerEvents(this, GameManager.getInstance());
        if(flickerWhenAvailable) {
            new BukkitRunnable() {
                boolean flickerOn = false;
                public void run() {
                    if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
                        this.cancel();
                        return;
                    }
                    flickerOn = !flickerOn;
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(SpectatorMode.getInstance().contains(player)) continue;
                        if(flickerOn) {
                            player.setExp(1f);
                        } else {
                            player.setExp(0);
                        }
                    }
                }
            }.runTaskTimer(GameManager.getInstance(), 0, 10L);
        }
    }

    public void reload(Player player, int totalTicks) {
        reloading.add(player);
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(ticks >= totalTicks) {
                    this.cancel();
                    reloading.remove(player);
                } else {
                    ticks++;
                    player.setExp(((float)ticks/(float)totalTicks));
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    public boolean isReloaded(Player player) {
        return !reloading.contains(player);
    }

    @EventHandler
    public void onSpectatorModeEnter(SpectatorModeEnterEvent event) {
        if(reloading.contains(event.getPlayer())) {
            reloading.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        HandlerList.unregisterAll(this);
    }
}