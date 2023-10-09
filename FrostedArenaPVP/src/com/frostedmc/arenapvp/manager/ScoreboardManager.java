package com.frostedmc.arenapvp.manager;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.arenapvp.arena.elo.EloManager;
import com.frostedmc.arenapvp.arena.elo.EloProfile;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.core.utils.SimpleScoreboard;
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

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
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

    private int onlineCache;

    private ScoreboardManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.playerBoards = new HashMap<Player, SimpleScoreboard>();
        this.onlineCache = Bukkit.getOnlinePlayers().size();

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
                    if(!ArenaManager.getInstance().inArena(player)) {
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
        }.runTaskLater(ArenaPVP.getInstance(), 3L);
    }

    private void registerScoreboard(Player player) {
        try {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        } catch (Exception e) {}

        BlinkEffect effect = new BlinkEffect("ArenaPVP", ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
        effect.next();

        AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
        SimpleScoreboard board = new SimpleScoreboard(effect.next());
        this.setDefaultLines(board, accountDetails);
        board.player = player;
        board.update();
        board.send(player);
        this.playerBoards.put(player, board);

        new BukkitRunnable() {
            public void run() {
                if(player.isOnline()) {
                    if(player.getScoreboard() != null) {
                        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(effect.next());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 3L);
    }

    public void setGameLines(SimpleScoreboard board, String fighting, String kit, String map) {
        board.reset();
        board.add("&2&m&m-----------------", 7);
        board.add("&7Fighting &b" + fighting, 6);
        board.add("&3Starting in » &f5.0s", 5);
        board.add("&eKit » &f" + kit, 4);
        board.add("&6Map » &f" + map, 3);
        board.add("&b&r&2&m-----------------", 2);
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    public void setDefaultLines(SimpleScoreboard board, AccountDetails accountDetails) {
        board.reset();
        int last = 5;
        for(int i=0; i<Kit.values().length; i++) {
            int line = (5+i);
            EloProfile eloProfile = EloManager.getInstance().fetchProfile(accountDetails.getUUID());
            int val = eloProfile.getElo(Kit.values()[i]);
            if(val == -1) {
                board.add("&f" + Kit.values()[i].getName() + " Elo » &7---", line);
            } else {
                board.add("&f" + Kit.values()[i].getName() + " Elo » &7" + val, line);
            }
            last = line;
        }
        board.add("&2&m&m-----------------", last+1);
        board.add("&b&r&2&m-----------------", 4);
        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        if(accountDetails.getRank() == Rank.PLAYER) {
            board.add("&6Rank » &7&lPLAYER", 2);
        } else {
            board.add("&6Rank » &7" + accountDetails.getRank().getPrefix(false), 2);
        }
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    public boolean hasScoreboard(Player player) {
        return this.playerBoards.containsKey(player);
    }
}