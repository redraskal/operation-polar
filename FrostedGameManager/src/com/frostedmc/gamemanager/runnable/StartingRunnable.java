package com.frostedmc.gamemanager.runnable;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.utils.Title;
import com.frostedmc.core.utils.title.TitleAnimator;
import com.frostedmc.core.utils.title.defaults.BlinkAnimation;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.listener.Join;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingRunnable extends BukkitRunnable {

    private String gameName;

    public StartingRunnable() {
        this.gameName = GameManager.getInstance().getCurrentGame().gameName();
        GameManager.getInstance().gameStatus = GameStatus.STARTING;
        GameManager.getInstance().publishServer();
        this.runTaskTimer(GameManager.getInstance(), 0, 20L);
    }

    private int countdown = GameManager.getInstance().getCurrentGame().gameFlags.startCountdown;

    @Override
    public void run() {
        if(GameManager.getInstance().getCurrentGame() == null
                || (GameManager.getInstance().getCurrentGame() != null
                && !GameManager.getInstance().getCurrentGame().gameName().equalsIgnoreCase(gameName))) {
            this.cancel();
        }
        if(GameManager.getInstance().gameStatus != GameStatus.STARTING) {
            this.cancel();
            this.resetStuff();
        } else {
            if(Bukkit.getOnlinePlayers().size() < GameManager.getInstance().getCurrentGame().gameFlags.minPlayers
                    && !GameManager.getInstance().getCurrentGame().gameFlags.forceStart) {
                this.cancel();
                this.resetStuff();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title title = new Title("Game has been cancelled.", "" + ChatColor.BLUE, 1, 1, 1);
                    title.setTitleColor(ChatColor.RED);
                    title.send(player);
                    player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 10, 1);
                    player.playSound(player.getLocation(), Sound.EXPLODE, 10, 1);
                }
            } else {
                if(countdown <= 0) {
                    this.cancel();
                    GameManager.getInstance().gameStatus = GameStatus.INGAME;
                    GameManager.getInstance().getServer().getPluginManager().registerEvents(GameManager.getInstance().getCurrentGame(),
                            GameManager.getInstance());
                    GameManager.getInstance().getCurrentGame().onGameStart();
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
                            JukeboxManager.getInstance()
                                    .update(player.getName(), SoundType.SOUND_EFFECT,
                                            "https://downloads.frostedmc.com/music/PreGame.mp3");
                        }
                    }
                    if(countdown == 7) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            new TitleAnimator(player, GameManager.getInstance(),
                                    new BlinkAnimation("Game starts in...",
                                            ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY,
                                            true, 0, null, Sound.FIREWORK_LAUNCH, Sound.FIREWORK_BLAST));
                        }
                    }
                    if(countdown <= 5) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            Title title = new Title("" + ChatColor.BLUE, "" + countdown + "...", 0, 1, 0);
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
                            title.send(player);
                        }
                    }
                    if(countdown == 30 || countdown == 15 || countdown == 10 || countdown <= 5) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                        }
                    }
                    if(countdown == 3) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 10, 1);
                        }
                    }
                    countdown--;
                }
            }
        }
    }

    private void resetStuff() {
        GameManager.getInstance().gameStatus = GameStatus.WAITING;
        GameManager.getInstance().publishServer();
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