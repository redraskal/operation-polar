package com.frostedmc.nightfall;

import com.frostedmc.core.Core;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.ChatModule;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.module.defaults.TabModule;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.nightfall.command.EntityTestCommand;
import com.frostedmc.nightfall.command.SchematicTestCommand;
import com.frostedmc.nightfall.command.WorldTestCommand;
import com.frostedmc.nightfall.manager.ScoreboardManager;
import com.frostedmc.nightfall.manager.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Nightfall extends JavaPlugin {

    @Getter private ScoreboardManager scoreboardManager;
    @Getter private WorldManager worldManager;

    public void onEnable() {
        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ChatModule(this),
                new TabModule(this)
        });

        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        MessageChannel.initialize(this);

        this.scoreboardManager = new ScoreboardManager(this);
        this.worldManager = new WorldManager(this);

        CommandManager.getInstance().registerCommand(new WorldTestCommand());
        CommandManager.getInstance().registerCommand(new SchematicTestCommand());
        CommandManager.getInstance().registerCommand(new EntityTestCommand());

        new BukkitRunnable() {
            public void run() {
                publishServer();
            }
        }.runTaskTimer(this, 0, 120L);
    }

    public void publishServerUpdate(String channel, String message) {
        this.getLogger().info("[Redis] Publishing to [" + channel + "]: " + message);
        Jedis temp = new Jedis("127.0.0.1", 6379, 60);
        //temp.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        temp.publish(channel, message);
        temp.quit();
    }

    public void publishServer() {
        this.publishServerUpdate("server-status", Bukkit.getServerName() + ":"
                + Bukkit.getOnlinePlayers().size()
                + ":" + Bukkit.getMaxPlayers()
                + ":0"
                + ":Nightfall");
    }

    public void onDisable() {
        Core.getInstance().disable();
        Bukkit.getOnlinePlayers().forEach(player -> this.getWorldManager().unloadFortressWorld(player.getUniqueId()));
    }
}
