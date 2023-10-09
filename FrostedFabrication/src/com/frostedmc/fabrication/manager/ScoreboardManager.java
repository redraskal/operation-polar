package com.frostedmc.fabrication.manager;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.redis.RedisCallback;
import com.frostedmc.core.api.redis.RedisServerManager;
import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.core.utils.PlayerBoard;
import com.frostedmc.fabrication.game.GameStatus;
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
 * Created by Redraskal_2 on 8/29/2016.
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
    private Map<Player, PlayerBoard> playerBoards;

    private int onlineCache;

    private ScoreboardManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.playerBoards = new HashMap<Player, PlayerBoard>();
        this.onlineCache = 0;

        long start = System.currentTimeMillis();
        Core.getInstance().getLogger().info("[Manager] Enabling Scoreboard Manager...");
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.registerScoreboard(player);
        }

        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    RedisServerManager.getInstance().queryOnlinePlayers(new RedisCallback() {
                        @Override
                        public void callback(Object data) {
                            if(onlineCache != (int) data) {
                                onlineCache = (int) data;

                                for(Player player : playerBoards.keySet()) {
                                    if(player.isOnline()) {
                                        if(hasScoreboard(player)) {
                                            update(player, UpdateType.ONLINE, "" + data);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 80L);

        long end = System.currentTimeMillis();
        long time = (end - start);
        Core.getInstance().getLogger().info("[Manager] Enabled Scoreboard Manager in " + time + " ms.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        this.registerScoreboard(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if(this.hasScoreboard(playerQuitEvent.getPlayer())) {
            this.playerBoards.remove(playerQuitEvent.getPlayer());
        }
    }

    private void registerScoreboard(Player player) {
        try {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        } catch (Exception e) {}

        BlinkEffect effect = new BlinkEffect("FABRICATION", ChatColor.WHITE, ChatColor.AQUA, ChatColor.GRAY);
        effect.next();

        AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
        PlayerBoard board = new PlayerBoard(player, effect.next());
        board.setLine(6, "&3");
        board.setLine(5, "&aWaiting for players");
        board.setLine(4, "&2");
        board.setLine(3, "&eOnline » &7" + this.onlineCache);
        board.setLine(2, "&6Server » &7" + Bukkit.getServerName());
        board.setLine(1, "&1");
        board.setLine(0, "&bwww.frostedmc.com");

        board.build();
        board.send();
        this.playerBoards.put(player, board);

        new BukkitRunnable() {
            public void run() {
                if(player.isOnline()) {
                    player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(effect.next());
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 3L);

        new BukkitRunnable() {
            int dots = 1;
            public void run() {
                if(player.isOnline()) {
                    if(GameStatus.GLOBAL == GameStatus.WAITING) {
                        String d = "";
                        for(int i=0; i<dots; i++) { d=d+"."; }
                        if(dots >= 3) { dots = 0; } else { dots++; }
                        ActionBar.send(player, "&aWaiting for players" + d);
                    }

                    if(GameStatus.GLOBAL == GameStatus.INGAME) {
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 20L);
    }

    public boolean hasScoreboard(Player player) {
        return this.playerBoards.containsKey(player);
    }

    public enum UpdateType {
        STATUS(5, "%value%"),
        ONLINE(3, "&eOnline » &7%value%"),
        SERVER(2, "&6Server » &7%value%");

        private int line;
        private String template;

        private UpdateType(int line, String template) {
            this.line = line;
            this.template = template;
        }

        public int getLine() {
            return this.line;
        }

        public String format(String value) {
            return this.template.replace("%value%", value);
        }
    }

    public boolean update(Player player, UpdateType updateType, String value) {
       if(this.hasScoreboard(player)) {
           this.playerBoards.get(player).setLine(updateType.getLine(), updateType.format(value));
           this.update(player);
           return true;
       }

       return false;
    }

    private boolean update(Player player) {
        if(this.hasScoreboard(player)) {
            this.playerBoards.get(player).update();
            return true;
        }

        return false;
    }
}