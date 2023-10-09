package com.frostedmc.kingdoms;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.ChatModule;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.module.defaults.TabModule;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.kingdoms.commands.ConfigCommand;
import com.frostedmc.kingdoms.commands.IcicleTransferCommand;
import com.frostedmc.kingdoms.listener.Join;
import com.frostedmc.kingdoms.listener.Move;
import com.frostedmc.kingdoms.listener.Quit;
import com.frostedmc.kingdoms.listener.Weather;
import com.frostedmc.kingdoms.module.GlacierHook;
import com.frostedmc.kingdoms.module.PermissionHook;
import com.frostedmc.kingdoms.module.PlaceholderAPIHook;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class FrostedKingdoms extends JavaPlugin {

    private static FrostedKingdoms instance;
    public static FrostedKingdoms getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ChatModule(this),
                new GlacierHook(this),
                new PermissionHook(this),
                new PlaceholderAPIHook(this),
                new TabModule(this)
        });
        Core.getInstance().getLogger().info("[FrostedGames] Initializing core engines...");
        CommandManager.initialize(this);
        ItemCreator.initialize(this);
        MessageChannel.initialize(this);
        StatisticsCache.initialize(this);

        CommandManager.getInstance().registerCommand(new ConfigCommand());
        CommandManager.getInstance().registerCommand(new IcicleTransferCommand());

        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new Weather(), this);
        this.getServer().getPluginManager().registerEvents(new Move(), this);
    }
}