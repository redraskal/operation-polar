package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Timestamp;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.module.Module;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class JoinModule extends Module implements Listener {

    private Backbone instance;

    public JoinModule(Backbone instance) {
        this.instance = instance;
    }

    @Override
    public String name() {
        return "Join Handler";
    }

    @Override
    public void onEnable() {
        this.instance.getProxy().getPluginManager().registerListener(this.instance, this);
    }

    @Override
    public void onDisable() {
        this.instance.getProxy().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if(ProxyServer.getInstance().getServers().size() > 0) {
            ServerInfo toChoose = KickModule.findServer(event.getKickedFrom().getName(), "Hub-");
            if(toChoose != null && !checkReason(event.getKickReasonComponent())) {
                event.setCancelled(true);
                event.setCancelServer(toChoose);
            } else {
                ServerInfo backupChoice = KickModule.findServer(event.getKickedFrom().getName(), "Thinking-");
                if(backupChoice != null && !checkReason(event.getKickReasonComponent())) {
                    event.setCancelled(true);
                    event.setCancelServer(toChoose);
                } else {
                    event.getPlayer().disconnect(new TextComponent(event.getKickReasonComponent()));
                }
            }
        }
    }

    private boolean checkReason(BaseComponent[] arr) {
        for(BaseComponent line : arr) {
            if(line.toPlainText().contains("Server Closed")
                    || line.toPlainText().contains("Server closed")
                    || line.toPlainText().contains("java")
                    || line.toPlainText().contains("Lost connection to server.")) {
                return false;
            }
        }
        return true;
    }

    private boolean ipBanned(String ip_address) {
        PreparedStatement statement = null;
        try {
            statement = Core.getInstance().getSQLConnection().getConnection().prepareStatement(
                    "SELECT * FROM `ip_bans` WHERE ip_address='" + ip_address + "'");
            try {
                ResultSet result = statement.executeQuery();
                if(result.next()) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    @EventHandler
    public void onPreLogin(ServerConnectEvent event) {
        if(!event.getTarget().getName().equalsIgnoreCase("undefined")) return;
        if(!Core.getInstance().getAccountManager().isRegistered(event.getPlayer().getUniqueId())) {
            Core.getInstance().getAccountManager().register(event.getPlayer().getUniqueId(),
                    event.getPlayer().getName(), event.getPlayer().getAddress().getHostString());
        }
        if(ipBanned(event.getPlayer().getPendingConnection().getAddress().getHostString())) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&lBan Severity over 9000\n&7You are banned &aforever&7.\n&7Reason: &eIP Banned\n\n&fYou cannot appeal this ban.")));
            return;
        }
        String glacierRecord = Core.getInstance().getGlacierPunishmentManager().parsePunishmentRecord(event.getPlayer().getUniqueId());
        if(!glacierRecord.isEmpty()) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', glacierRecord)));
            return;
        }
        String punishment = Core.getInstance().getPunishmentManager().hasOngoingBan(event.getPlayer().getUniqueId());
        if(!punishment.isEmpty()) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', punishment)));
            return;
        }
        if(Core.getInstance().getAccountManager().isRegistered(event.getPlayer().getUniqueId())) {
            Core.getInstance().getAccountManager().update(event.getPlayer().getUniqueId(), new UpdateDetails(UpdateDetails.UpdateType.USERNAME, event.getPlayer().getName()));
            Core.getInstance().getAccountManager().update(event.getPlayer().getUniqueId(), new UpdateDetails(UpdateDetails.UpdateType.IP_ADDRESS, event.getPlayer().getAddress().getHostString()));
            Core.getInstance().getAccountManager().update(event.getPlayer().getUniqueId(), new UpdateDetails(UpdateDetails.UpdateType.LAST_SEEN, Timestamp.getCurrentTimestamp().toString()));
        }
        if(!onlineServers() && ProxyServer.getInstance().getServers().size() > 0) {
            ServerInfo toChoose = randomThinking();
            if(toChoose != null) {
                event.setTarget(toChoose);
            } else {
                event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cThere are no servers available to connect to.")));
            }
        } else {
            ServerInfo toChoose = chooseJoinServer();
            if(toChoose != null) {
                event.setTarget(toChoose);
            } else {
                event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cThere are no servers available to connect to.")));
            }
        }
    }

    public static ServerInfo chooseJoinServer() {
        return KickModule.findServer("", "Hub-");
    }

    public static ServerInfo randomThinking() {
        return KickModule.findServer("", "Thinking-");
    }

    public static boolean onlineServers() {
        for(ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if(!serverInfo.getName().contains("Thinking") && !serverInfo.getName().equalsIgnoreCase("undefined")) {
                return true;
            }
        }
        return false;
    }
}