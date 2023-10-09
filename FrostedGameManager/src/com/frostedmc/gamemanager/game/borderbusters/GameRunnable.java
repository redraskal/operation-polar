package com.frostedmc.gamemanager.game.borderbusters;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class GameRunnable extends BukkitRunnable {

    public static GameRunnable instance;
    public int countdown = 8;

    public GameRunnable() {
        instance = this;
        instance.runTaskTimer(GameManager.getInstance(), 0, 20L);
    }

    @Override
    public void run() {
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            if(countdown == 8) {
                Title title = new Title("",
                        "Stage " + ((BorderBusters) GameManager.getInstance().getCurrentGame()).stage, 1, 1, 1);
                title.setSubtitleColor(ChatColor.WHITE);
                title.broadcast();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5, 1);
                    player.setLevel(((BorderBusters) GameManager.getInstance().getCurrentGame()).stage);
                    if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                        ScoreboardManager.getInstance().playerBoards.get(player).add("&fStage: &a"
                                + ((BorderBusters) GameManager.getInstance().getCurrentGame()).stage, 5);
                        ScoreboardManager.getInstance().playerBoards.get(player).update();
                    }
                }
            }
            if(countdown != -1) {
                if(countdown <= 1) {
                    countdown = -1;
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 5, 1.5f);
                    }
                    new StageRunnable();
                    return;
                } else {
                    countdown--;
                }
                if(countdown != -1 && countdown <= 5) {
                    Title title = new Title("", "Next round in " + countdown + " seconds...");
                    if(countdown == 5) {
                        title.setSubtitleColor(ChatColor.DARK_GREEN);
                    }
                    if(countdown == 4) {
                        title.setSubtitleColor(ChatColor.GREEN);
                    }
                    if(countdown == 3) {
                        title.setSubtitleColor(ChatColor.YELLOW);
                    }
                    if(countdown == 2) {
                        title.setSubtitleColor(ChatColor.RED);
                    }
                    if(countdown == 1) {
                        title.setSubtitleColor(ChatColor.DARK_RED);
                    }
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 5, 1);
                    }
                    title.broadcast();
                }
            }
        } else {
            this.cancel();
        }
    }
}