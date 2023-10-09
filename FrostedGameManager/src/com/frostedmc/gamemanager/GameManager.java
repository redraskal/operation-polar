package com.frostedmc.gamemanager;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.*;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.commands.GameCommand;
import com.frostedmc.gamemanager.commands.GodModeCommand;
import com.frostedmc.gamemanager.event.GameEndEvent;
import com.frostedmc.gamemanager.game.aerial.AerialDomination;
import com.frostedmc.gamemanager.game.borderbusters.BorderBusters;
import com.frostedmc.gamemanager.game.oitc.OITC;
import com.frostedmc.gamemanager.game.parkourmania.ParkourMania;
import com.frostedmc.gamemanager.game.rocketroyal.RocketRoyal;
import com.frostedmc.gamemanager.game.spacewarfare.SpaceWarfare;
import com.frostedmc.gamemanager.listener.*;
import com.frostedmc.gamemanager.manager.DamageManager;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import com.frostedmc.gamemanager.runnable.StartingRunnable;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 9/5/2016.
 */
public class GameManager extends JavaPlugin {

    private static GameManager instance;
    public static GameManager getInstance() {
        return instance;
    }

    public Location lobbySpawn = null;
    public Jedis jedis;
    private List<Class<? extends Game>> gameListing = new ArrayList<Class<? extends Game>>();
    private List<Class<? extends Game>> arcadeListing = new ArrayList<Class<? extends Game>>();
    public GameStatus gameStatus = GameStatus.WAITING;
    public boolean arcadeMode = false;
    private int currentArcadeGame = 0;
    private Game currentGame;
    public Game getCurrentGame() { return this.currentGame; }

    public List<Class<? extends Game>> getGameListing() {
        return this.gameListing;
    }

    public boolean forceStart() {
        if(currentGame == null) return false;
        if(gameStatus != GameStatus.WAITING) return false;
        currentGame.gameFlags.forceStart = true;
        new StartingRunnable();
        return true;
    }

    public void setCurrentGame(Game game) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isDead()) player.spigot().respawn();
            player.teleport(lobbySpawn);
        }
        boolean sendMessage = false;
        if(this.currentGame != null) {
            if(!currentGame.gameName().equalsIgnoreCase(game.gameName())) {
                sendMessage = true;
            }
            currentGame.unloadMaps();
            currentGame.onGameUnload();
            NametagModule nametagModule = (NametagModule) Core.getInstance().getModule(NametagModule.class);
            for(Player player : Bukkit.getOnlinePlayers()) {
                nametagModule.updateTagCache(player);
            }
        }
        this.gameStatus = GameStatus.WAITING;
        this.currentGame = game;
        GameManager.getInstance().publishServer();
        for(Player player : Bukkit.getOnlinePlayers()) {
            SpectatorMode.getInstance().remove(player);
            Join.resetHotbar(player);
            Join.setWaitingInventory(player);
            if (ScoreboardManager.getInstance().hasScoreboard(player)) {
                ScoreboardManager.getInstance()
                        .setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
            if(sendMessage) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7The game has been set to &e"
                        + currentGame.gameName() + "&7."));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
            }
            currentGame.onPlayerJoinLobby(player);
        }
        currentGame.onGameLoad();
        if(Bukkit.getOnlinePlayers().size() >= GameManager.getInstance().getCurrentGame().gameFlags.minPlayers) {
            new StartingRunnable();
        }
    }

    public void endGame(Location fireworks) {
        if(this.currentGame == null) return;
        if(gameStatus != GameStatus.INGAME) return;
        HandlerList.unregisterAll(this.currentGame);
        this.getServer().getPluginManager().callEvent(new GameEndEvent());
        gameStatus = GameStatus.ENDED;
        Move.dontAllow.clear();
        GameManager.getInstance().publishServer();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!SpectatorMode.getInstance().contains(player)) {
                SpectatorMode.getInstance().add(player);
            }
        }
        currentGame.onGameEnd();
        DamageManager.shootFireworks(fireworks);
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel(0);
            player.setExp(0);
            player.setHealthScale(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            if (ScoreboardManager.getInstance().hasScoreboard(player)) {
                ScoreboardManager.getInstance()
                        .setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player)
                        .add("&aGame has ended", 5);
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
        }
        NametagModule nametagModule = (NametagModule) Core.getInstance().getModule(NametagModule.class);
        Bukkit.getOnlinePlayers().forEach(player -> nametagModule.revertTag(player));
        new BukkitRunnable() {
            public void run() {
                try {
                    if(arcadeMode) {
                        if((currentArcadeGame+1) >= arcadeListing.size()) {
                            currentArcadeGame = 0;
                        } else {
                            currentArcadeGame++;
                        }
                        setCurrentGame(arcadeListing.get(currentArcadeGame).newInstance());
                    } else {
                        setCurrentGame(currentGame.getClass().newInstance());
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(GameManager.getInstance(), 20*8);
    }

    public void onEnable() {
        instance = this;
        lobbySpawn = new Location(Bukkit.getWorld("world"), 43.5, 183, -122.5, (float) 90.0, (float) -1.0);
        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new ChatModule(this),
                new GlacierHook(this),
                new TabModule(this)
        });
        Core.getInstance().getLogger().info("[GameManager] Initializing core engines...");
        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        ScoreboardManager.initialize(this);
        MessageChannel.initialize(this);
        SpectatorMode.initialize(this);
        StatisticsCache.initialize(this);

        this.getLogger().info("[Redis] Connecting to server...");
        jedis = new Jedis("127.0.0.1");
        this.getLogger().info("[Redis] Authenticating...");
        //jedis.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        this.getLogger().info("[Redis] Ready to publish requests.");

        for(World world : Bukkit.getWorlds()) {
            world.setStorm(false);
            for(Entity entity : world.getEntities()) {
                if(entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }

        this.getServer().getPluginManager().registerEvents(new Block(), this);
        this.getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        this.getServer().getPluginManager().registerEvents(new Item(), this);
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new TNT(), this);
        this.getServer().getPluginManager().registerEvents(new Weather(), this);
        this.getServer().getPluginManager().registerEvents(new Damage(), this);
        this.getServer().getPluginManager().registerEvents(new Move(), this);

        CommandManager.getInstance().registerCommand(new GameCommand());
        CommandManager.getInstance().registerCommand(new GodModeCommand());

        // Register games
        this.gameListing.add(OITC.class);
        this.gameListing.add(BorderBusters.class);
        this.gameListing.add(ParkourMania.class);
        this.gameListing.add(SpaceWarfare.class);
        this.gameListing.add(AerialDomination.class);
        this.gameListing.add(RocketRoyal.class);

        // Register arcade mode
        this.arcadeListing.add(OITC.class);
        this.arcadeListing.add(BorderBusters.class);
        this.arcadeListing.add(ParkourMania.class);
        //this.arcadeListing.add(SpaceWarfare.class);

        Core.getInstance().getLogger().info("[Channel] Verifying server name...");
        String serverName = Bukkit.getServerName();
        boolean found = false;

        for(Class<? extends Game> game : this.gameListing) {
            try {
                Game temp = game.newInstance();
                if(temp.gameName().replace(" ", "").equalsIgnoreCase(serverName.split("-")[0])) {
                    found = true;
                    setCurrentGame(temp);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(serverName.split("-")[0].equalsIgnoreCase("Arcade")) {
            found = true;
            arcadeMode = true;
            try {
                setCurrentGame(this.arcadeListing.get(0).newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(!found) {
            Core.getInstance().getLogger().severe("[Channel] Server verification failed.");
        }

        new BukkitRunnable() {
            public void run() {
                GameManager.getInstance().publishServer();
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 120L);
    }

    public boolean isGameLoaded(Game game) {
        if(game != null && this.currentGame != null
                && game.getGameUUID() == this.currentGame.getGameUUID()) {
            return true;
        } else {
            return false;
        }
    }

    public void publishServerUpdate(String channel, String message) {
        this.getLogger().info("[Redis] Publishing to [" + channel + "]: " + message);
        jedis.publish(channel, message);
    }

    public void publishServer() {
        int maxPlayers = Bukkit.getOnlinePlayers().size()+1;
        if(GameManager.getInstance().getCurrentGame() != null) {
            maxPlayers = GameManager.getInstance().getCurrentGame().gameFlags.maxPlayers;
        }
        String game = "NA";
        if(GameManager.getInstance().getCurrentGame() != null) {
            game = GameManager.getInstance().getCurrentGame().gameName();
        }
        this.publishServerUpdate("server-status", Bukkit.getServerName() + ":"
                + Bukkit.getOnlinePlayers().size()
                + ":" + maxPlayers
                + ":" + gameStatus.getID()
                + ":" + game);
    }

    public void onDisable() {
        Core.getInstance().disable();
        this.getLogger().info("[Redis] Disconnecting from server...");
        jedis.quit();
    }
}