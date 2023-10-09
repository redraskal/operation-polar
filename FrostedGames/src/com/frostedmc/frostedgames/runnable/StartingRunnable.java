package com.frostedmc.frostedgames.runnable;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.District;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.SpectatorMode;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.listener.Join;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class StartingRunnable extends BukkitRunnable {

    public StartingRunnable() {
        InternalGameSettings.status = Status.STARTING;
        FrostedGames.getInstance().publishServerUpdate();
        this.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    private int countdown = InternalGameSettings.countdown;

    @Override
    public void run() {
        if(InternalGameSettings.status != Status.STARTING) {
            this.cancel();
            this.resetStuff();
        } else {
            if(Bukkit.getOnlinePlayers().size() < InternalGameSettings.minPlayers) {
                this.cancel();
                this.resetStuff();
            } else {
                if(countdown <= 0) {
                    this.cancel();
                    InternalGameSettings.status = Status.INGAME;
                    FrostedGames.getInstance().publishServerUpdate();
                    List<Player> randomized = new ArrayList<Player>();
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        randomized.add(player);
                    }
                    Collections.shuffle(randomized);
                    if(InternalGameSettings.gameMaker == null) {
                        InternalGameSettings.gameMaker = randomized.toArray(new Player[randomized.size()])
                                [new Random().nextInt(randomized.size())];
                        randomized.remove(InternalGameSettings.gameMaker);
                    }
                    randomized.remove(InternalGameSettings.gameMaker);
                    ScoreboardManager.getInstance().playerBoards.get(InternalGameSettings.gameMaker)
                            .add("" + ChatColor.AQUA + ChatColor.BOLD + "[GM]", 2);
                    ScoreboardManager.getInstance().playerBoards.get(InternalGameSettings.gameMaker).update();
                    NametagModule module = (NametagModule) Core.getInstance().getModule(NametagModule.class);
                    module.updateTag(InternalGameSettings.gameMaker, " " + ChatColor.AQUA + ChatColor.BOLD + "[GM]");
                    InternalGameSettings.gameMaker.teleport(InternalGameSettings.map.getSpectator());
                    SpectatorMode.getInstance().add(InternalGameSettings.gameMaker);
                    Join.resetHotbar(InternalGameSettings.gameMaker);
                    Join.setGamemakerInventory(InternalGameSettings.gameMaker);
                    InternalGameSettings.gameMaker.setGameMode(GameMode.CREATIVE);
                    InternalGameSettings.gameMaker.setFlying(true);
                    int currentspawn = 0;
                    for(Player player : randomized) {
                        Join.resetHotbar(player);
                        player.getInventory().clear();
                        District district = District.DISTRICT_1;
                        int team = 0;
                        int current = -1;
                        if(!InternalGameSettings.districtMap.isEmpty()) {
                            for(Map.Entry<Player, District> entry : InternalGameSettings.districtMap.entrySet()) {
                                if(current % 2 == 0) {
                                    team++;
                                }
                                current++;
                            }
                            district = District.values()[team];
                        }
                        ScoreboardManager.getInstance()
                                .setGameLines(ScoreboardManager.getInstance().playerBoards.get(player),
                                        district);
                        ScoreboardManager.getInstance().playerBoards.get(player).update();
                        module.updateTag(player, " " + district.getTag());
                        InternalGameSettings.districtMap.put(player, district);
                        Location spawnpoint = null;
                        if(InternalGameSettings.map.getSpawnpoints().length >= currentspawn) {
                            spawnpoint = InternalGameSettings.map.getSpawnpoints()[currentspawn];
                        } else {
                            spawnpoint = InternalGameSettings.map.getSpawnpoints()
                                    [new Random().nextInt(InternalGameSettings.map.getSpawnpoints().length)];
                        }
                        player.teleport(spawnpoint);
                        currentspawn++;
                    }
                    new GameStartRunnable();
                    Bukkit.getWorld("game").setDifficulty(Difficulty.EASY);
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                            ScoreboardManager.getInstance().playerBoards.get(player)
                                    .add("&aStarting in " + countdown, 5);
                            ScoreboardManager.getInstance().playerBoards.get(player).update();
                        }
                    }
                    if(countdown == 23) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            JukeboxManager.getInstance().update(player.getName(), SoundType.SOUND_EFFECT, "https://downloads.frostedmc.com/music/PreGame.mp3");
                        }
                    }
                    if(countdown == 3) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 5, 1);
                        }
                    }
                    countdown--;
                }
            }
        }
    }

    private void resetStuff() {
        InternalGameSettings.status = Status.WAITING;
        FrostedGames.getInstance().publishServerUpdate();
        for(Player player : Bukkit.getOnlinePlayers()) {
            Join.resetHotbar(player);
            Join.setWaitingInventory(player);
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                ScoreboardManager.getInstance()
                        .setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
        }
    }
}