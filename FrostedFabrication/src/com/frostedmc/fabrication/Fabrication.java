package com.frostedmc.fabrication;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.redis.RedisServerManager;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.*;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.fabrication.commands.SkullCommand;
import com.frostedmc.fabrication.game.Arena;
import com.frostedmc.fabrication.game.Countdown;
import com.frostedmc.fabrication.game.GameStatus;
import com.frostedmc.fabrication.listener.Falling;
import com.frostedmc.fabrication.listener.Items;
import com.frostedmc.fabrication.listener.Join;
import com.frostedmc.fabrication.listener.Quit;
import com.frostedmc.fabrication.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 8/27/2016.
 */
public class Fabrication extends JavaPlugin {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new NoHungerModule(this, 20),
                new NoDamageModule(this, 20),
                new AntiSpamModule(),
                new AntiTNTModule(this),
                new ChatModule(this),
        });

        Core.getInstance().getLogger().info("[Fabrication] Initializing core engines...");

        CommandManager.initialize(this);
        RedisServerManager.initialize(this);
        ScoreboardManager.initialize(this);
        ItemCreator.initialize(this);

        this.registerCommands();
        this.registerListeners();

        for(Player player : Bukkit.getOnlinePlayers()) { Join.reset(player); }
        if(Bukkit.getOnlinePlayers().size() >= GameStatus.MIN_PLAYERS) { new Countdown(); }
    }

    private void registerCommands() {
        CommandManager.getInstance().registerCommand(new SkullCommand());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new Join(), instance);
        this.getServer().getPluginManager().registerEvents(new Quit(), instance);
        this.getServer().getPluginManager().registerEvents(new Falling(), instance);
        this.getServer().getPluginManager().registerEvents(new Items(), instance);
    }

    public void onDisable() {
        for(Arena arena : Arena.getArenas()) { arena.clear(); }
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }
        Core.getInstance().disable();
    }
}