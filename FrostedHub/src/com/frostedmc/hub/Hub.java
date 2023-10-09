package com.frostedmc.hub;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.ItemCallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.gui.ItemListener;
import com.frostedmc.core.gui.JoinItem;
import com.frostedmc.core.gui.defaults.CosmeticsMainMenuGUI;
import com.frostedmc.core.gui.defaults.ProfileGUI;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.*;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.hub.gui.ServerViewGUI;
import com.frostedmc.hub.listener.Join;
import com.frostedmc.hub.listener.Move;
import com.frostedmc.hub.listener.Quit;
import com.frostedmc.hub.listener.SnowStuff;
import com.frostedmc.hub.manager.CustomPubSub;
import com.frostedmc.hub.manager.GamePubSub;
import com.frostedmc.hub.manager.ScoreboardManager;
import com.frostedmc.hub.module.*;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Redraskal_2 on 8/27/2016.
 */
public class Hub extends JavaPlugin {

    private static Hub instance;
    public static Hub getInstance() {
        return instance;
    }
    public static boolean allowLogin = false;

    public Jedis jedis;

    public void onEnable() {
        instance = this;

        Bukkit.getWorld("world").setGameRuleValue("doMobSpawning", "false");
        Bukkit.getWorld("world").setDifficulty(Difficulty.EASY);

        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new NametagModule(this),
                new ForceGamemodeModule(this, GameMode.ADVENTURE),
                new DoubleJumpModule(this, Sound.IRONGOLEM_THROW, 1),
                new NoHungerModule(this, 20),
                new NoDamageModule(this, 20),
                new ChatModule(this),
                new TabModule(this),
                new GlacierHook(this),
                new AntiMoveModule()
        });

        Core.getInstance().getLogger().info("[Hub] Initializing core engines...");

        CommandManager.initialize(this);
        ScoreboardManager.initialize(this);
        ItemCreator.initialize(this);
        MessageChannel.initialize(this);
        StatisticsCache.initialize(this);

        this.getLogger().info("[Redis] Connecting to server...");
        jedis = new Jedis("127.0.0.1", 6379, 60);
        this.getLogger().info("[Redis] Authenticating...");
        //jedis.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        this.getLogger().info("[Redis] Ready to receive requests.");

        this.setupItemListeners();
        this.setupHotbar();

        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Quit(), this);
        this.getServer().getPluginManager().registerEvents(new Move(), this);

        LocationSpawnModule locationSpawnModule = new LocationSpawnModule(this, new Location(
                Bukkit.getWorld("world"), 35.5, 95.0, 71.5, (float) 90, (float) 8));
        Core.getInstance().enableModule(locationSpawnModule);

        new BukkitRunnable() {
            public void run() {
                CustomPubSub customPubSub = new CustomPubSub();
                GamePubSub gamePubSub = new GamePubSub();
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if(channel.equalsIgnoreCase("server-cat-status")) {
                            customPubSub.onMessage(channel, message);
                        }
                        if(channel.equalsIgnoreCase("server-status")) {
                            gamePubSub.onMessage(channel, message);
                        }
                    }
                };
                jedis.subscribe(jedisPubSub, "server-cat-status", "server-status");
            }
        }.runTaskAsynchronously(this);

        new SnowStuff();
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    Core.getInstance().enableModule(new FrostyModule(Hub.getInstance()));
                    Core.getInstance().enableModule(new FortuneTellerModule());
                    Core.getInstance().enableModule(new CasualGamesModule());
                    Core.getInstance().enableModule(new CardGamesModule());
                    Core.getInstance().enableModule(new UndefinedGamesModule());
                    Core.getInstance().enableModule(new NPCModule(Hub.getInstance()));
                    new BukkitRunnable() {
                        public void run() {
                            for(Entity entity : Bukkit.getWorld("world").getEntities()) {
                                if(entity instanceof ArmorStand) {
                                    if(entity.hasMetadata("updating-players")) {
                                        ArmorStand nameTag = (ArmorStand) entity;
                                        nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e"
                                                + NPCModule.players(nameTag.getMetadata("template").get(0).asString())
                                                + " players"));
                                        nameTag.setCustomNameVisible(true);
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Hub.getInstance(), 0, 60L);
                }
            }
        }.runTaskTimer(Hub.getInstance(), 0, 20L);
        new BukkitRunnable() {
            public void run() {
                Hub.getInstance().publishServer();
            }
        }.runTaskTimer(Hub.getInstance(), 0, 120L);
    }

    private void setupHotbar() {
        Core.getInstance().getLogger().info("[Hub] Setting up hotbar...");

        new JoinItem(this, ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&a&lGAME SELECTOR"), 0, true);
        new JoinItem(this, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&2&lLOBBY SELECTOR"), 1, true);
        //new JoinItem(this, ItemCreator.getInstance().createItem(Material.SLIME_BALL, 1, 0, "&e&lCOSMETICS"), 7, true);
        new JoinItem(this, ItemCreator.getInstance().createItem(Material.BREWING_STAND_ITEM, 1, 0, "&6&lNETWORK LEVELING"), 8, true);
    }

    private void setupItemListeners() {
        Core.getInstance().getLogger().info("[Hub] Setting up item listeners...");

        new ItemListener(this, "LOBBY SELECTOR", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new ServerViewGUI(player, "Hub Servers", "Hub-");
                }
            }
        });

        new ItemListener(this, "GAME SELECTOR", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new com.frostedmc.hub.gui.GameSelectorGUI(player, instance);
                }
            }
        });

        new ItemListener(this, "COSMETICS", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new CosmeticsMainMenuGUI(player, instance);
                }
            }
        });

        new ItemListener(this, "PROFILE", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    new ProfileGUI(player, Hub.getInstance());
                }
            }
        });

        new ItemListener(this, "NETWORK LEVELING", false, false, new ItemCallback() {
            @Override
            public void callback(Player player, Action action) {
                if(action == Action.RIGHT_CLICK_BLOCK
                        || action == Action.RIGHT_CLICK_AIR) {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bServer> &7This feature is coming soon."));
                }
            }
        });
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
                + ":Hub");
    }

    public void onDisable() {
        Core.getInstance().disable();
        this.getLogger().info("[Redis] Disconnecting from server...");
        jedis.quit();
    }
}