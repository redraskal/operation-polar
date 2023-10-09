package com.frostedmc.lobby;

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
import com.frostedmc.lobby.listener.*;
import com.frostedmc.lobby.manager.CustomPubSub;
import com.frostedmc.lobby.manager.ScoreboardManager;
import com.frostedmc.lobby.manager.SignManager;
import com.frostedmc.lobby.module.CharacterSelectorModule;
import com.frostedmc.lobby.module.GlacierHook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class FrostedLobby extends JavaPlugin {

    private static FrostedLobby instance;
    public static FrostedLobby getInstance() {
        return instance;
    }

    public Location LOBBY_SPAWN = null;
    public Jedis jedis;

    public void onEnable() {
        instance = this;
        LOBBY_SPAWN = new Location(Bukkit.getWorld("world"), 52.5, 54.0, -324.5, (float) -179.3, (float) 7.8);
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new ChatModule(this),
                new GlacierHook(this),
                new TabModule(this),
                new CharacterSelectorModule(this)
        });
        Core.getInstance().getLogger().info("[FrostedGames] Initializing core engines...");
        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        ScoreboardManager.initialize(this);
        SignManager.initialize(this);
        SignManager.getInstance().scan(new Location(Bukkit.getWorld("world"), 53, 48, -353), 15);
        MessageChannel.initialize(this);

        this.getLogger().info("[Redis] Connecting to server...");
        jedis = new Jedis("127.0.0.1");
        this.getLogger().info("[Redis] Authenticating...");
        jedis.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        this.getLogger().info("[Redis] Ready to receive requests.");

        for(World world : Bukkit.getWorlds()) {
            world.setStorm(false);
        }

        this.getServer().getPluginManager().registerEvents(new Damage(), this);
        this.getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        this.getServer().getPluginManager().registerEvents(new Item(), this);
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new Weather(), this);
        this.getServer().getPluginManager().registerEvents(new Block(), this);
        this.getServer().getPluginManager().registerEvents(new SignHandler(), this);

        new BukkitRunnable() {
            public void run() {
                jedis.subscribe(new CustomPubSub(), "fg-server");
            }
        }.runTaskAsynchronously(this);
    }

    public void onDisable() {
        Core.getInstance().disable();
        this.getLogger().info("[Redis] Disconnecting from server...");
        jedis.quit();
    }
}