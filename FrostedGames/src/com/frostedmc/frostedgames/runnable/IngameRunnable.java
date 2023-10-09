package com.frostedmc.frostedgames.runnable;

import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class IngameRunnable extends BukkitRunnable {

    public IngameRunnable() {
        this.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    private int minutes = InternalGameSettings.ingameMinutes;
    private int seconds = 0;

    @Override
    public void run() {
        if(InternalGameSettings.status == Status.ENDED) {
            this.cancel();
            return;
        }
        if(minutes == 0 && seconds <= 0) {
            this.cancel();
            new SuddenDeathRunnable();
        } else {
            String min = "";
            if(minutes < 10) {
                min = "0" + minutes;
            } else {
                min = "" + minutes;
            }
            String sec = "";
            if(seconds < 10) {
                sec = "0" + seconds;
            } else {
                sec = "" + seconds;
            }
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&fDeathmatch: &a" + min + "&f:&a" + sec, 5);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
            }
            if(minutes == 0 && seconds == 3) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 5, 1);
                }
            }
            if(InternalGameSettings.districtMap.size() <= 3 && minutes > 1) {
                minutes = 1;
                seconds = 0;
            } else {
                if(seconds <= 0) {
                    minutes--;
                    seconds = 59;
                } else {
                    seconds--;
                }
            }
        }
    }
}