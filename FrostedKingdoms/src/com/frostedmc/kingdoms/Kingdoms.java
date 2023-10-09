package com.frostedmc.kingdoms;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.redis.RedisServerManager;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.glacier.GlacierModule;
import com.frostedmc.core.gui.ItemCallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.gui.ItemListener;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.AntiSpamModule;
import com.frostedmc.core.module.defaults.ChatModule;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.kingdoms.backend.KingdomDataManager;
import com.frostedmc.kingdoms.commands.TestCommand;
import com.frostedmc.kingdoms.gui.ProcessingPaymentGUI;
import com.frostedmc.kingdoms.listener.CustomTNT;
import com.frostedmc.kingdoms.listener.Hunger;
import com.frostedmc.kingdoms.listener.Join;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Redraskal_2 on 11/13/2016.
 */
public class Kingdoms extends JavaPlugin {

    private static Kingdoms instance;

    public static Kingdoms getInstance() {
        return instance;
    }

    public Location SPAWN_LOCATION;

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("play.frostedmc.com", 3306, "backbone", "DogsGoWoof20383", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new AntiSpamModule(),
                new ChatModule(this),
        });

        Core.getInstance().getLogger().info("[Kingdoms] Initializing core engines...");

        CommandManager.initialize(this);
        CommandManager.getInstance().registerCommand(new TestCommand());

        RedisServerManager.initialize(this);
        ItemCreator.initialize(this);

        this.createDirectories();
        KingdomDataManager.getInstance().reloadCache();

        this.registerListeners();
        this.setupItemListeners();
        this.SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), 567.5, 92, 976.5,
                (float) -0.2, (float) 2.8);

        Core.getInstance().enableModule(new GlacierModule(this));
    }

    private void createDirectories() {
        File kingdomData = new File(this.getDataFolder().getPath() + "/kingdom_data/");
        File playerData = new File(this.getDataFolder().getPath() + "/player_data/");

        if(!kingdomData.exists())
            kingdomData.mkdirs();
        if(!playerData.exists())
            playerData.mkdirs();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Hunger(), this);
        this.getServer().getPluginManager().registerEvents(new CustomTNT(), this);
    }

    private void setupItemListeners() {
        Core.getInstance().getLogger().info("[Kingdoms] Setting up item listeners...");

        new ItemListener(this, "Create a Kingdom (Right-Click)", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if (action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new ProcessingPaymentGUI(player, Kingdoms.getInstance());
                }
            }
        });
    }

    public void onDisable() {
        Core.getInstance().disable();
    }

    public static void reset(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealthScale(20);
        player.setHealth(20);
    }
}