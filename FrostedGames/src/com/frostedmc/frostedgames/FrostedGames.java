package com.frostedmc.frostedgames;

import com.frostedmc.core.Core;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.ChatModule;
import com.frostedmc.core.module.defaults.ForceGamemodeModule;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.module.defaults.TabModule;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.frostedgames.command.ForceGamemakerCommand;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Map;
import com.frostedmc.frostedgames.game.SpectatorMode;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.listener.*;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import com.frostedmc.frostedgames.module.GlacierHook;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.Random;

/**
 * Created by Redraskal_2 on 1/20/2017.
 */
public class FrostedGames extends JavaPlugin {

    private static FrostedGames instance;
    public static FrostedGames getInstance() {
        return instance;
    }

    public Location LOBBY_SPAWN = null;
    public Jedis jedis;

    public void onEnable() {
        instance = this;
        LOBBY_SPAWN = new Location(Bukkit.getWorld("world"), 43.5, 183, -122.5, (float) 90.0, (float) -1.0);
        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new ChatModule(this),
                new GlacierHook(this),
                new TabModule(this)
        });
        Core.getInstance().getLogger().info("[FrostedGames] Initializing core engines...");
        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        ScoreboardManager.initialize(this);
        MessageChannel.initialize(this);

        CommandManager.getInstance().registerCommand(new ForceGamemakerCommand());

        for(World world : Bukkit.getWorlds()) {
            world.setStorm(false);
            for(Entity entity : world.getEntities()) {
                if(entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }

        InternalGameSettings.map = Map.values()[new Random().nextInt(Map.values().length)];
        File mapFolder = new File("/home/maps/"
                + InternalGameSettings.map.toString().toLowerCase() + "/");
        Bukkit.broadcastMessage("Copying map from " + mapFolder.getAbsoluteFile().getPath());
        FileUtils.copyDirectory(mapFolder.getAbsoluteFile(),
                new File(Bukkit.getWorldContainer().getPath() + "/game/"));
        this.getServer().createWorld(new WorldCreator("game"));
        this.getServer().createWorld(new WorldCreator("deathmatch"));

        Bukkit.getWorld("game").setGameRuleValue("doFireTick", "false");
        Bukkit.getWorld("game").setGameRuleValue("doMobSpawning", "false");
        Bukkit.getWorld("game").setGameRuleValue("randomTickSpeed", "0");
        Bukkit.getWorld("deathmatch").setGameRuleValue("doFireTick", "false");
        Bukkit.getWorld("deathmatch").setGameRuleValue("doMobSpawning", "false");
        Bukkit.getWorld("deathmatch").setGameRuleValue("randomTickSpeed", "0");

        SpectatorMode.initialize(this);

        this.getLogger().info("[Redis] Connecting to server...");
        jedis = new Jedis("127.0.0.1");
        this.getLogger().info("[Redis] Authenticating...");
        //jedis.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        this.getLogger().info("[Redis] Ready to publish requests.");

        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        this.getServer().getPluginManager().registerEvents(new Damage(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new Weather(), this);
        this.getServer().getPluginManager().registerEvents(new Block(), this);
        this.getServer().getPluginManager().registerEvents(new com.frostedmc.frostedgames.listener.Item(), this);
        this.getServer().getPluginManager().registerEvents(new TNT(), this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&cThis server is now being restarted."));
        }

        this.publishServerUpdate();

        new BukkitRunnable() {
            public void run() {
                publishServerUpdate();
            }
        }.runTaskTimer(this, 0, 120L);
    }

    public void publishServerUpdate() {
        jedis.publish("fg-server", Bukkit.getServerName()
                + "," + Bukkit.getOnlinePlayers().size()
                + "," + InternalGameSettings.status.getID()
                + "," + InternalGameSettings.map.getName());
    }

    public void onDisable() {
        Core.getInstance().disable();
        jedis.publish("fg-server", Bukkit.getServerName()
                + "," + Bukkit.getOnlinePlayers().size()
                + "," + Status.ENDED.getID()
                + "," + InternalGameSettings.map.getName());
        this.getLogger().info("[Redis] Disconnecting from server...");
        jedis.quit();
    }
}