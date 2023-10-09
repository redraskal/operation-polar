package com.frostedmc.thinkinghouse;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.redis.RedisServerManager;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.gui.ItemListener;
import com.frostedmc.core.gui.JoinItem;
import com.frostedmc.core.gui.defaults.LoadingGUI;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.*;
import com.frostedmc.core.sql.SQLDetails;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class ThinkingHouse extends JavaPlugin implements Listener {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NoDamageModule(this, 20),
                new NoHungerModule(this, 20),
                new NoStreamModule(this),
                new NoMessagesModule(this),
                new NoHotbarModule(this),
                new AntiTNTModule(this),
                new HidePlayersModule(this),
                new NametagModule(this),
                new NoInteractModule(this),
                new LocationSpawnModule(this, new Location(Bukkit.getWorld("world"), -785.633, 5.0, 471.254, (float) 178.8, (float) 2.0)),
        });

        Core.getInstance().getLogger().info("[ThinkingHouse] Initializing core engines...");

        CommandManager.initialize(this);
        RedisServerManager.initialize(this);
        ItemCreator.initialize(this);

        this.setupItemListeners();
        this.setupHotbar();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &b"));
        playerJoinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &e&lWelcome to CV's Thinking House™"));
        playerJoinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &7A nice world for you to think."));
        playerJoinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &b"));
    }

    private void setupHotbar() {
        Core.getInstance().getLogger().info("[ThinkingHouse] Setting up hotbar...");

        new JoinItem(this, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&2&lHUBS &7(Right-Click)"), 8, true);
    }

    private void setupItemListeners() {
        Core.getInstance().getLogger().info("[ThinkingHouse] Setting up item listeners...");

        new ItemListener(this, "HUBS (Right-Click)", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new LoadingGUI(player, instance);
                }
            }
        });
    }

    public void onDisable() {
        Core.getInstance().disable();
    }
}