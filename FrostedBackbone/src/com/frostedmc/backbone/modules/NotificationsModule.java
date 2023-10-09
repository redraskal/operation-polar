package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.backbone.api.misc.BungeeStatisticsCache;
import com.frostedmc.core.Core;
import com.frostedmc.core.module.Module;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class NotificationsModule extends Module implements Listener {

    @Override
    public String name() {
        return "Notifications";
    }

    @Override
    public void onEnable() {
        Backbone.getInstance().getProxy().getPluginManager().registerListener(Backbone.getInstance(), this);
        BungeeCord.getInstance().registerChannel("report-notify");
        BungeeCord.getInstance().registerChannel("glacier-notify");
        RedisBungee.getApi().registerPubSubChannels("report-notify");
        RedisBungee.getApi().registerPubSubChannels("glacier-notify");
    }

    @Override
    public void onDisable() {
        Backbone.getInstance().getProxy().getPluginManager().unregisterListener(this);
        BungeeCord.getInstance().unregisterChannel("report-notify");
        BungeeCord.getInstance().unregisterChannel("glacier-notify");
        RedisBungee.getApi().unregisterPubSubChannels("report-notify");
        RedisBungee.getApi().unregisterPubSubChannels("glacier-notify");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("report-notify")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                String username = utf.split(",")[0];
                UUID reporting = UUID.fromString(utf.split(",")[1]);
                RedisBungee.getApi().sendChannelMessage("report-notify", username + "," + reporting.toString());
            } catch (Exception e) {}
        }
        if (event.getTag().equalsIgnoreCase("glacier-notify")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                RedisBungee.getApi().sendChannelMessage("glacier-notify", utf);
            } catch (Exception e) {}
        }
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if(event.getChannel().equalsIgnoreCase("report-notify")) {
            announceReport(event.getMessage().split(",")[0],
                    UUID.fromString(event.getMessage().split(",")[1]));
        }
        if(event.getChannel().equalsIgnoreCase("glacier-notify")) {
            announceGlacierNotification(event.getMessage());
        }
    }

    private void announceReport(String username, UUID reporting) {
        String report = "unknown";
        try {
            ResultSet resultSet =
                    Core.getInstance().getUUIDFetcher().queryAccountDetails(reporting);
            if(resultSet.next()) {
                report = resultSet.getString("username");
            }
        } catch (Exception e) {}
        for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if(BungeeStatisticsCache.getInstance() != null) {
                if(BungeeStatisticsCache.getInstance()
                        .get(proxiedPlayer.getUniqueId(), "options_player_reports") == 1) {
                    proxiedPlayer.sendMessage(ChatMessageType.CHAT,
                            new TextComponent(ChatColor.translateAlternateColorCodes('&',
                                    "&bReport> &e" + username + " &7has opened a report against &e"
                                            + report + "&7.")));
                    proxiedPlayer.sendMessage(ComponentSerializer.parse(
                            "[\"\",{\"text\":\"Report> \",\"color\":\"aqua\"},{\"text\":\"Click here \",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/viewreport " + username + " " + report + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Left-Click to view.\"}]}}},{\"text\":\"to review the report.\",\"color\":\"gray\"}]"));
                }
            }
        }
    }

    private void announceGlacierNotification(String message) {
        for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (BungeeStatisticsCache.getInstance() != null) {
                if (BungeeStatisticsCache.getInstance()
                        .get(proxiedPlayer.getUniqueId(), "options_glacier_reports") == 1) {
                    if(message.contains("{")) {
                        proxiedPlayer.sendMessage(ComponentSerializer.parse(message));
                    } else {
                        proxiedPlayer.sendMessage(ChatMessageType.CHAT,
                                new TextComponent(ChatColor.translateAlternateColorCodes('&',
                                        message)));
                    }
                }
            }
        }
    }
}