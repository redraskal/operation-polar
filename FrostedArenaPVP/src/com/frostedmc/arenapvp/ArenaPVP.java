package com.frostedmc.arenapvp;

import com.frostedmc.arenapvp.arena.Game;
import com.frostedmc.arenapvp.commands.DuelCommand;
import com.frostedmc.arenapvp.commands.PracticeCommand;
import com.frostedmc.arenapvp.commands.SpectateCommand;
import com.frostedmc.arenapvp.arena.elo.EloManager;
import com.frostedmc.arenapvp.listener.*;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.arenapvp.manager.ScoreboardManager;
import com.frostedmc.arenapvp.manager.SignManager;
import com.frostedmc.arenapvp.module.GlacierHook;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.*;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class ArenaPVP extends JavaPlugin {

    private static ArenaPVP instance;

    public static ArenaPVP getInstance() {
        return instance;
    }

    public Location LOBBY_SPAWN = null;

    public void onEnable() {
        instance = this;
        LOBBY_SPAWN = new Location(Bukkit.getWorld("world"), -214.5, 26, 644.5, (float) -176.7, (float) 1.8);
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new ChatModule(this),
                new GlacierHook(this),
                new TabModule(this)
        });
        Core.getInstance().getLogger().info("[ArenaPVP] Initializing core engines...");
        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        ArenaManager.initialize(this);
        ScoreboardManager.initialize(this);
        SignManager.initialize(this);
        SignManager.getInstance().scan(new Location(Bukkit.getWorld("world"), -215, 21.5, 607), 10);
        EloManager.getInstance();
        MessageChannel.initialize(this);
        StatisticsCache.initialize(this);

        CommandManager.getInstance().registerCommand(new SpectateCommand());
        CommandManager.getInstance().registerCommand(new DuelCommand());
        CommandManager.getInstance().registerCommand(new PracticeCommand());

        this.getServer().getPluginManager().registerEvents(new Damage(), this);
        this.getServer().getPluginManager().registerEvents(new Hunger(), this);
        this.getServer().getPluginManager().registerEvents(new Item(), this);
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new Creative(), this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            Game.resetInv(player);
            Game.lobbyInv(player);
            player.teleport(LOBBY_SPAWN);
        }

        for(World world : Bukkit.getWorlds()) {
            world.setStorm(false);
        }
    }

    public void onDisable() {
        Core.getInstance().disable();
    }
}