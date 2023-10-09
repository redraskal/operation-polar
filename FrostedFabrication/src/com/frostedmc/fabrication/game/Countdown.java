package com.frostedmc.fabrication.game;

import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.gui.VoteGUI;
import com.frostedmc.fabrication.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class Countdown {

    public Countdown() {
        if(GameStatus.GLOBAL == GameStatus.STARTING) {
            return;
        }

        GameStatus.GLOBAL = GameStatus.STARTING;
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() < GameStatus.MIN_PLAYERS) {
                    this.cancel();
                    GameStatus.GLOBAL = GameStatus.WAITING;
                    GameStatus.COUNTDOWN = GameStatus.DEFAULT_COUNTDOWN;
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        ScoreboardManager.getInstance().update(player, ScoreboardManager.UpdateType.STATUS, "&aWaiting for players"); }
                    return;
                }

                if(GameStatus.COUNTDOWN <= 1) {
                    this.cancel();
                    GameStatus.GLOBAL = GameStatus.INGAME;
                    int c = 1;
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        ActionBar.send(player, "&aThe game has begun.");
                        Spawnpoint spawnpoint = Spawnpoint.valueOf("ARENA_" + c);
                        player.teleport(spawnpoint.get());
                        player.setGameMode(GameMode.CREATIVE);
                        player.getInventory().clear();
                        new Arena(player, spawnpoint);
                        c++;
                    }
                    new VoteGUI();
                    new IngameCountdown();
                } else {
                    GameStatus.COUNTDOWN--;
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        ActionBar.send(player, "&aStarting in " + GameStatus.COUNTDOWN);
                        ScoreboardManager.getInstance().update(player, ScoreboardManager.UpdateType.STATUS, "&aStarting in " + GameStatus.COUNTDOWN); }
                }
            }
        }.runTaskTimer(Fabrication.getInstance(), 0, 20L);
    }
}
