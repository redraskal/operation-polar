package com.frostedmc.nightfall.manager;

import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.nightfall.Nightfall;
import com.frostedmc.nightfall.listener.ScoreboardManagerListener;
import com.frostedmc.nightfall.utils.SimpleScoreboard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ScoreboardManager {

    @Getter private final Nightfall nightfall;
    @Getter private Map<UUID, SimpleScoreboard> scoreboardMap = new HashMap<>();
    private BlinkEffect blinkEffect;

    public ScoreboardManager(Nightfall nightfall) {
        this.nightfall = nightfall;
        this.getNightfall().getServer().getPluginManager()
                .registerEvents(new ScoreboardManagerListener(this),
                        this.getNightfall());

        this.blinkEffect = new BlinkEffect("NIGHTFALL",
                ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
        this.blinkEffect.next();
        new BukkitRunnable() {
            public void run() {
                final String next = blinkEffect.next();
                getScoreboardMap().values().forEach(simpleScoreboard
                        -> simpleScoreboard.setTitle(next));
            }
        }.runTaskTimer(this.getNightfall(), 0, 3L);
    }

    public void registerScoreboard(Player player) {
        if(scoreboardMap.containsKey(player.getUniqueId())) return;
        SimpleScoreboard simpleScoreboard = new SimpleScoreboard(blinkEffect.getDefault());

        simpleScoreboard.add("&4", 10);

        if(this.getNightfall().getWorldManager().doesPlayerHaveWorld(player.getUniqueId())) {
            simpleScoreboard.add("&9&lFortress Health", 9);
            simpleScoreboard.add("&8[&7||||||||||||&8] &b0%", 8);
            simpleScoreboard.add("&3", 7);
            simpleScoreboard.add("&3&lDefense Level", 6);
            simpleScoreboard.add("&7Lvl. 0 &8» &b0 XP left", 5);
        } else {
            simpleScoreboard.add("You do not own a", 9);
            simpleScoreboard.add("&n&lFortress&r right now.", 8);
            simpleScoreboard.add("&3", 7);
            simpleScoreboard.add("Create one with", 6);
            simpleScoreboard.add("the &n&lCompass&r.", 5);
        }

        simpleScoreboard.add("&2", 4);
        simpleScoreboard.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        simpleScoreboard.add("&6Server » &7" + Bukkit.getServerName(), 2);
        simpleScoreboard.add("&1", 1);
        simpleScoreboard.add("&bwww.frostedmc.com", 0);

        simpleScoreboard.send(player);
        simpleScoreboard.update();
        scoreboardMap.put(player.getUniqueId(), simpleScoreboard);
    }

    public SimpleScoreboard getScoreboard(Player player) {
        if(!scoreboardMap.containsKey(player.getUniqueId())) this.registerScoreboard(player);
        return scoreboardMap.get(player.getUniqueId());
    }

    public void removeScoreboard(Player player) {
        if(!scoreboardMap.containsKey(player.getUniqueId())) return;
        scoreboardMap.get(player.getUniqueId()).reset();
        scoreboardMap.remove(player.getUniqueId());
    }
}