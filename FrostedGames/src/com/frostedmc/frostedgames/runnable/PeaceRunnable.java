package com.frostedmc.frostedgames.runnable;

import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class PeaceRunnable extends BukkitRunnable {

    public PeaceRunnable() {
        this.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    private int countdown = InternalGameSettings.peace;

    @Override
    public void run() {
        if(InternalGameSettings.status == Status.ENDED) {
            this.cancel();
            return;
        }
        if(countdown <= 0) {
            this.cancel();
            InternalGameSettings.enablePeace = false;
            new IngameRunnable();
        } else {
            String co = "";
            if(countdown < 10) {
                co = "0" + countdown;
            } else {
                co = "" + countdown;
            }
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&fPeace: &a00&f:&a" + co, 5);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
            }
            countdown--;
        }
    }
}