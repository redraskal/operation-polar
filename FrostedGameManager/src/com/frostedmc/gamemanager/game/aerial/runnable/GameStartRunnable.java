package com.frostedmc.gamemanager.game.aerial.runnable;

import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.game.aerial.AerialDomination;
import com.frostedmc.gamemanager.listener.Move;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GameStartRunnable {

    public GameStartRunnable(AerialDomination aerialDomination) {
        new BukkitRunnable() {
            public void run() {
                Title title = new Title("", "❄ Aerial : Domination ❄", 0, 4, 0);
                title.setSubtitleColor(ChatColor.DARK_AQUA);
                title.broadcast();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                    ChatUtils.sendBlockMessage("Aerial : Domination", new String[]{
                            "&bClaim the capture point",
                            "&bbefore it's too late!"
                    }, player);
                }
                new BukkitRunnable() {
                    public void run() {
                        new BukkitRunnable() {
                            int countdown = 3;
                            public void run() {
                                if(countdown <= 0) {
                                    this.cancel();
                                    Move.dontAllow.clear();
                                    for(Player player : Bukkit.getOnlinePlayers()) {
                                        player.setLevel(0);
                                        player.setExp(0);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
                                        player.setAllowFlight(true);
                                    }
                                    Title title = new Title("", "GO!", 0, 1, 0);
                                    title.setSubtitleColor(ChatColor.GREEN);
                                    title.broadcast();
                                    //TODO
                                } else {
                                    if(countdown == 3) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(3);
                                            player.setExp(0.9f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "3...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.RED);
                                        title.broadcast();
                                    }
                                    if(countdown == 2) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(2);
                                            player.setExp(0.6f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "2...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.YELLOW);
                                        title.broadcast();
                                    }
                                    if(countdown == 1) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(1);
                                            player.setExp(0.3f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "1...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.DARK_GREEN);
                                        title.broadcast();
                                    }
                                    countdown--;
                                }
                            }
                        }.runTaskTimer(GameManager.getInstance(), 0, 20L);
                    }
                }.runTaskLater(GameManager.getInstance(), 40L);
            }
        }.runTaskLater(GameManager.getInstance(), 60L);
    }
}