package com.frostedmc.fabrication.game;

import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.core.utils.Title;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class IngameCountdown implements Listener {

    private IngameCountdown instance;

    public IngameCountdown() {
        this.instance = this;
        for(Player player : Bukkit.getOnlinePlayers()) {
            ScoreboardManager.getInstance().update(player, ScoreboardManager.UpdateType.STATUS, "&3Theme » &eChoosing");
        }

        new BukkitRunnable() {
            public void run() {
                if(GameStatus.SECONDS <= 0) {
                    if(GameStatus.MINUTES > 0) {
                        GameStatus.MINUTES--;
                        GameStatus.SECONDS = 59;
                    } else {
                        this.cancel();
                        Fabrication.getInstance().getServer().getPluginManager().registerEvents(instance, Fabrication.getInstance());
                        Title title = new Title("", "Stop building!", 0, 2, 0);
                        title.setSubtitleColor(ChatColor.WHITE);
                        title.broadcast();

                        new BukkitRunnable() {
                            public void run() {
                                Title title = new Title("", "Voting will begin shortly.", 0, 2, 2);
                                title.setSubtitleColor(ChatColor.WHITE);
                                title.broadcast();
                            }
                        }.runTaskLater(Fabrication.getInstance(), 40L);

                        new BukkitRunnable() {
                            public void run() {
                                new Voting();
                            }
                        }.runTaskLater(Fabrication.getInstance(), 120L);
                    }
                } else {
                    GameStatus.SECONDS--;
                }

                update();
            }
        }.runTaskTimer(Fabrication.getInstance(), 0, 20L);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        blockPlaceEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        blockBreakEvent.setCancelled(true);
    }

    private void update() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            ActionBar.send(player, "&3Remaining » &a"
                    + getMinutes()
                    + "&7:&a" + getSeconds());
        }
    }

    public String getMinutes() {
        String min = "" + GameStatus.MINUTES;
        if(GameStatus.MINUTES < 10) {
            min = "0" + min;
        }
        return min;
    }

    public String getSeconds() {
        String sec = "" + GameStatus.SECONDS;
        if(GameStatus.SECONDS < 10) {
            sec = "0" + sec;
        }
        return sec;
    }
}
