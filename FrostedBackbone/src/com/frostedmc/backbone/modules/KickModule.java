package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.backbone.api.misc.BungeeStatisticsCache;
import com.frostedmc.backbone.automate.ServerInfo;
import com.frostedmc.backbone.automate.ServerManager;
import com.frostedmc.backbone.automate.TemplateManager;
import com.frostedmc.core.module.Module;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class KickModule extends Module implements Listener {

    private Backbone instance;

    public KickModule(Backbone instance) {
        this.instance = instance;
    }

    @Override
    public String name() {
        return "Kick";
    }

    @Override
    public void onEnable() {
        this.instance.getProxy().getPluginManager().registerListener(this.instance, this);
        BungeeCord.getInstance().registerChannel("custom-kick");
        BungeeCord.getInstance().registerChannel("custom-message");
        BungeeCord.getInstance().registerChannel("custom-switch");
        BungeeCord.getInstance().registerChannel("options-update");
        //BungeeCord.getInstance().registerChannel("custom-rca");
    }

    @Override
    public void onDisable() {
        this.instance.getProxy().getPluginManager().unregisterListener(this);
        BungeeCord.getInstance().unregisterChannel("custom-kick");
        BungeeCord.getInstance().unregisterChannel("custom-message");
        BungeeCord.getInstance().unregisterChannel("custom-switch");
        BungeeCord.getInstance().unregisterChannel("options-update");
        //BungeeCord.getInstance().unregisterChannel("custom-rca");
    }

    public static net.md_5.bungee.api.config.ServerInfo findServer(String currentServer, String prefix) {
        List<net.md_5.bungee.api.config.ServerInfo> choose = new ArrayList<net.md_5.bungee.api.config.ServerInfo>();
        for(net.md_5.bungee.api.config.ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if(serverInfo.getName().startsWith(prefix)) {
                choose.add(serverInfo);
            }
        }
        if(choose.size() == 0) {
            return null;
        } else {
            net.md_5.bungee.api.config.ServerInfo picked = null;
            for(net.md_5.bungee.api.config.ServerInfo serverInfo : choose) {
                if(serverInfo.getName().equalsIgnoreCase(currentServer)) continue;
                if(picked == null) {
                    picked = serverInfo;
                    continue;
                }
                if(serverInfo.getPlayers().size() < picked.getPlayers().size()) picked = serverInfo;
            }
            return picked;
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getTag().equalsIgnoreCase("options-update")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                String username = utf.split(",")[0];
                String option = utf.split(",")[1];
                if(BungeeStatisticsCache.getInstance() != null) {
                    if(ProxyServer.getInstance().getPlayer(username) != null) {
                        BungeeStatisticsCache.getInstance().update(ProxyServer.getInstance().getPlayer(username)
                                        .getUniqueId(),
                                option);
                    }
                }
            } catch (Exception e) {}
        }
        if(event.getTag().equalsIgnoreCase("custom-switch")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                String prefix = in.readUTF();
                if(ProxyServer.getInstance().getPlayer(utf) != null) {
                    net.md_5.bungee.api.config.ServerInfo serverInfo =
                            findServer(ProxyServer.getInstance().getPlayer(utf).getServer().getInfo().getName(), prefix);
                    if(serverInfo != null) {
                        ProxyServer.getInstance().getPlayer(utf).connect(serverInfo);
                    }
                }
            } catch (IOException e) {}
        }
        if(event.getTag().equalsIgnoreCase("custom-rca")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                Backbone.getInstance().getProxy().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
                    public void run() {
                        if(ServerManager.getInstance().doesServerExist(utf)) {
                            if(ServerManager.getInstance().getServer(utf).isStarted()) {
                                ServerManager.getInstance().stopServer(ServerManager.getInstance().getServer(utf));
                            }
                            ServerManager.getInstance().deleteServer(ServerManager.getInstance().getServer(utf));
                            ServerInfo serverInfo = ServerManager.getInstance()
                                    .createServer(TemplateManager.getInstance().getTemplate(utf.split("-")[0]), utf);
                            ServerManager.getInstance().startServer(serverInfo);
                        }
                    }
                }, 6, TimeUnit.SECONDS);
            } catch (IOException e) {}
        }
        if(event.getTag().equalsIgnoreCase("custom-kick")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                String message = "";
                for(int i=1; i<utf.split(":").length; i++) {
                    if(message.isEmpty()) {
                        message = utf.split(":")[i];
                    } else {
                        message+=" "+utf.split(":")[i];
                    }
                }
                for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if(proxiedPlayer.getName().equalsIgnoreCase(utf.split(":")[0]) || proxiedPlayer.getAddress().getHostString().equalsIgnoreCase(utf.split(":")[0])) {
                        ProxyServer.getInstance().getPlayer(utf.split(":")[0])
                                .disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
                    }
                }
            } catch (IOException e) {}
        }
        if(event.getTag().equalsIgnoreCase("custom-message")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                String message = "";
                for(int i=1; i<utf.split(":").length; i++) {
                    if(message.isEmpty()) {
                        message = utf.split(":")[i];
                    } else {
                        message+=" "+utf.split(":")[i];
                    }
                }
                for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if(proxiedPlayer.getName().equalsIgnoreCase(utf.split(":")[0]) || proxiedPlayer.getAddress().getHostString().equalsIgnoreCase(utf.split(":")[0])) {
                        ProxyServer.getInstance().getPlayer(utf.split(":")[0])
                                .sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
                    }
                }
            } catch (IOException e) {}
        }
    }
}