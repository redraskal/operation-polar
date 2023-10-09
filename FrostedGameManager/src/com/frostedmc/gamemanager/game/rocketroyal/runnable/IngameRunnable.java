package com.frostedmc.gamemanager.game.rocketroyal.runnable;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.game.rocketroyal.RocketRoyal;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class IngameRunnable extends BukkitRunnable {

    @Getter private final RocketRoyal rocketRoyal;

    private int minutes = 3;
    private int seconds = 0;

    public IngameRunnable(RocketRoyal rocketRoyal) {
        this.rocketRoyal = rocketRoyal;
        this.runTaskTimer(GameManager.getInstance(), 0, 20L);
    }

    @Override
    public void run() {
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&fTime Limit: " + buildTimer(), 10);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
            }
            if(seconds == 0) {
                if(minutes == 0) {
                    this.cancel();
                    GameManager.getInstance().endGame(GameManager.getInstance().getCurrentGame()
                            .getLoadedMaps().get(0).getSpectatorLocation().convert(GameManager.getInstance()
                                    .getCurrentGame()
                                    .getLoadedMaps().get(0).getInstance()));
                    java.util.Map<Player, Integer> sortedScores = Utils.sortByValue(getRocketRoyal().getKills());
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        int kills = 0;
                        if(getRocketRoyal().getKills().containsKey(player)) {
                            kills = getRocketRoyal().getKills().get(player);
                        }
                        int icicles = (kills*3)+10;
                        String firstPlace = "---";
                        String secondPlace = "---";
                        String thirdPlace = "---";
                        if(sortedScores.size() >= 1) {
                            firstPlace = ((Player) new ArrayList(sortedScores.keySet()).get(0)).getName();
                        }
                        if(sortedScores.size() >= 2) {
                            secondPlace = ((Player) new ArrayList(sortedScores.keySet()).get(1)).getName();
                        }
                        if(sortedScores.size() >= 3) {
                            thirdPlace = ((Player) new ArrayList(sortedScores.keySet()).get(2)).getName();
                        }
                        if(player.getName().equalsIgnoreCase(firstPlace)) {
                            icicles+=9;
                        }
                        if(player.getName().equalsIgnoreCase(secondPlace)) {
                            icicles+=6;
                        }
                        if(player.getName().equalsIgnoreCase(thirdPlace)) {
                            icicles+=3;
                        }
                        Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                        (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId())
                                                .getIcicles()+icicles)));
                        ChatUtils.sendBlockMessage("Game Summary", new String[]{
                                "&a&l1st Place: &3" + firstPlace,
                                "&e&l2nd Place: &3" + secondPlace,
                                "&c&l3rd Place: &3" + thirdPlace,
                                "&7",
                                "&7You have killed &3" + kills + " &7player(s)!",
                                "&7You have earned &3" + icicles + " &7icicle(s)!"
                        }, player);
                    }
                } else {
                    minutes--;
                    seconds = 59;
                }
            } else {
                seconds--;
            }
        } else {
            this.cancel();
        }
    }

    private String buildTimer() {
        String min = "" + minutes;
        String sec = "" + seconds;
        if(minutes < 10) {
            min="0"+min;
        }
        if(seconds < 10) {
            sec="0"+sec;
        }
        return "&a" + min + "&f:&a" + sec;
    }
}
