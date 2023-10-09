package com.frostedmc.backbone;

import com.frostedmc.backbone.api.misc.BungeeStatisticsCache;
import com.frostedmc.backbone.api.misc.MOTDManager;
import com.frostedmc.backbone.api.misc.MaintenanceManager;
import com.frostedmc.backbone.automate.ServerInfo;
import com.frostedmc.backbone.automate.ServerManager;
import com.frostedmc.backbone.automate.TemplateManager;
import com.frostedmc.backbone.commands.*;
import com.frostedmc.backbone.modules.*;
import com.frostedmc.core.Core;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.sql.SQLDetails;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class Backbone extends Plugin {

    private static Backbone instance;
    public static Backbone getInstance() {
        return instance;
    }

    private String boxName = "BUNGEE-NA";
    private String ipAddress;
    private MOTDManager motdManager;
    private MaintenanceManager maintenanceManager;
    public boolean stopAutomate = false;

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("localhost", 3306, "serverWide", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new KickModule(this),
                new MuteModule(this),
                new TimeOnlineModule()
        });
        this.getDataFolder().mkdirs();

        Core.getInstance().getLogger().info("[Backbone] Initializing core engines...");

        Core.getInstance().getLogger().info("[Backbone] [Automater] Checking IP address...");
        this.ipAddress = this.getIPAddress();
        Core.getInstance().getLogger().info("[Backbone] [Automater] IP Address is now " + this.ipAddress + ".");

        if(this.ipAddress.equalsIgnoreCase("195.154.199.208")) {
            this.boxName = "BUNGEE-1";
        }

        Core.getInstance().getLogger().info("[Backbone] [Automater] Setting box name to " + this.boxName + ".");

        Core.getInstance().getLogger().info("[Manager] Enabling MOTD Manager...");
        this.motdManager = new MOTDManager();
        Core.getInstance().enableModule(new MOTDModule(this));

        Core.getInstance().getLogger().info("[Manager] Enabling Maintenance Manager...");
        this.maintenanceManager = new MaintenanceManager();
        Core.getInstance().enableModule(new MaintenanceModule(this));

        BungeeStatisticsCache.initialize(this);

        Core.getInstance().getLogger().info("[Manager] Registering commands...");
        this.getProxy().getPluginManager().registerCommand(this, new CloudCommand());
        this.getProxy().getPluginManager().registerCommand(this, new AlertCommand());
        this.getProxy().getPluginManager().registerCommand(this, new NetworkAlertCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ServerCommand());
        this.getProxy().getPluginManager().registerCommand(this, new StaffChatCommand());
        //this.getProxy().getPluginManager().registerCommand(this, new ReportCommand());
        this.getProxy().getPluginManager().registerCommand(this, new IPBanCommand());
        this.getProxy().getPluginManager().registerCommand(this, new AltCheckCommand());
        this.getProxy().getPluginManager().registerCommand(this, new MOTDCommand());
        this.getProxy().getPluginManager().registerCommand(this, new HubCommand());
        this.getProxy().getPluginManager().registerCommand(this, new LobbyCommand());
        this.getProxy().getPluginManager().registerCommand(this, new MessageCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
        this.getProxy().getPluginManager().registerCommand(this, new GotoCommand());

        //this.getProxy().getPluginManager().registerCommand(this, new SpamCommand());

        Core.getInstance().enableModule(new JoinModule(this));

        getProxy().getScheduler().schedule(this, new Runnable() {
            public void run() {
                Core.getInstance().enableModule(new PubSubModule(instance));
                Core.getInstance().enableModule(new TwoFactorAuthModule(instance));
                Core.getInstance().enableModule(new NotificationsModule());
                Core.getInstance().getLogger().info("[Manager] Enabling Automation Manager...");
                TemplateManager.getInstance();
                ServerManager.getInstance();
                //new ServerAutomater();
                Core.getInstance().getLogger()
                        .warning("[Backbone] [Automater] Disabled Server Automater for bug fixes.");
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void onDisable() {
        stopAutomate = true;
        for(ServerInfo serverInfo : ServerManager.getInstance().getServers(true)) {
            ServerManager.getInstance().stopServer(serverInfo);
        }
        Core.getInstance().disable();
    }

    private String getIPAddress() {
        Core.getInstance().getLogger().info("[Backbone] [Automater] Checking IP address...");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com")
                    .openStream()));
            return reader.readLine();
        } catch (Exception e) {
            Core.getInstance().getLogger().info("[Backbone] [Automater] Could not obtain IP address.");
            return "127.0.0.1";
        }
    }

    public String getBoxName() {
        return this.boxName;
    }

    public MOTDManager getMOTDManager() {
        return this.motdManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return this.maintenanceManager;
    }
}