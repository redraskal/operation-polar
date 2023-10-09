package com.frostedmc.nightfall.listener;

import com.frostedmc.nightfall.manager.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ScoreboardManagerListener implements Listener {

    @Getter private final ScoreboardManager scoreboardManager;

    public ScoreboardManagerListener(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getScoreboardManager().getScoreboardMap().values().forEach(
                simpleScoreboard -> {
                    simpleScoreboard.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
                    simpleScoreboard.update();
                });
        this.getScoreboardManager().registerScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getScoreboardManager().removeScoreboard(event.getPlayer());
        this.getScoreboardManager().getScoreboardMap().values().forEach(
                simpleScoreboard -> {
                    simpleScoreboard.add("&eOnline » &7" + (Bukkit.getOnlinePlayers().size()-1), 3);
                    simpleScoreboard.update();
                });
    }
}