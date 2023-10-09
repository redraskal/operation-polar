package com.frostedmc.gamemanager.manager;

import com.frostedmc.core.Core;
import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager implements Listener {

    private static ScoreboardManager instance = null;

    public static ScoreboardManager getInstance() {
        return instance;
    }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }
        instance = new ScoreboardManager(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;
    public Map<Player, SimpleScoreboard> playerBoards;

    private ScoreboardManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.playerBoards = new HashMap<Player, SimpleScoreboard>();

        long start = System.currentTimeMillis();
        Core.getInstance().getLogger().info("[Manager] Enabling Scoreboard Manager...");
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.registerScoreboard(player);
        }

        long end = System.currentTimeMillis();
        long time = (end - start);
        Core.getInstance().getLogger().info("[Manager] Enabled Scoreboard Manager in " + time + " ms.");
    }

    private void updatePlayerCount() {
        for(Player player : playerBoards.keySet()) {
            if(player.isOnline()) {
                if(hasScoreboard(player)) {
                    if(GameManager.getInstance().gameStatus == GameStatus.WAITING
                            || GameManager.getInstance().gameStatus == GameStatus.STARTING) {
                        SimpleScoreboard board = playerBoards.get(player);
                        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
                        board.update();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        this.updatePlayerCount();
        this.registerScoreboard(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if(this.hasScoreboard(playerQuitEvent.getPlayer())) {
            this.playerBoards.remove(playerQuitEvent.getPlayer());
        }
        new BukkitRunnable() {
            public void run() {
                updatePlayerCount();
            }
        }.runTaskLater(GameManager.getInstance(), 10L);
    }

    private void registerScoreboard(Player player) {
        try {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        } catch (Exception e) {}

        BlinkEffect tempEffect = new BlinkEffect("Loading...", ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
        tempEffect.next();

        SimpleScoreboard board = new SimpleScoreboard(tempEffect.next());
        this.setDefaultLines(board);
        board.player = player;
        board.update();
        board.send(player);
        this.playerBoards.put(player, board);

        new BukkitRunnable() {
            boolean first = true;
            BlinkEffect effect = new BlinkEffect("Loading...", ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
            public void run() {
                if(first) {
                    first = false;
                    effect.next();
                }
                if(player.isOnline()) {
                    if(player.getScoreboard() != null) {
                        if(effect.getDefault().equalsIgnoreCase("Loading...")
                                || (GameManager.getInstance().getCurrentGame() != null
                                    && !GameManager.getInstance().getCurrentGame().gameName().equalsIgnoreCase(effect.getDefault()))) {
                            if(GameManager.getInstance().getCurrentGame() != null) {
                                effect = new BlinkEffect(GameManager.getInstance().getCurrentGame().gameName(), ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
                                effect.next();
                            }
                        }
                        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(effect.next());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 3L);
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&3", 6);
        board.add("&aWaiting for players", 5);
        board.add("&2", 4);
        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        board.add("&6Server » &7" + Bukkit.getServerName(), 2);
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    public boolean hasScoreboard(Player player) {
        return this.playerBoards.containsKey(player);
    }
}