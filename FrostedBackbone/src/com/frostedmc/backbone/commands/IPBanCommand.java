package com.frostedmc.backbone.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Redraskal_2 on 1/14/2017.
 */
public class IPBanCommand extends Command {

    public IPBanCommand() {
        super("banip");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if (!Rank.compare(rank, Rank.ADMIN)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7You need &c&lADMIN&7 to execute this action.")
                ));
                return;
            }
        }
        if(strings.length >= 1) {
            boolean found = false;
            String ip_address = "";
            ResultSet resultSet = Core.getInstance().getAccountManager().queryAccountDetails(strings[0]);
            try {
                if(resultSet.next()) {
                    ip_address = resultSet.getString("ip");
                    found = true;
                }
            } catch (Exception e) {}
            if(!found) {
                ResultSet resultSet2 = Core.getInstance().getAccountManager().queryAccountDetailsAddress(strings[0]);
                try {
                    if(resultSet2.next()) {
                        ip_address = resultSet2.getString("ip");
                        found = true;
                    }
                } catch (Exception e) {}
            }
            if(!found) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7We could not find a username/IP address with &e" + strings[0] + "&7."));
            } else {
                ResultSet resultSet2 = Core.getInstance().getAccountManager().queryAccountDetailsAddress(strings[0]);
                boolean record = false;
                try {
                    if(resultSet2.next()) {
                        record = true;
                    }
                } catch (Exception e) {}
                if(!record) {
                    try {
                        PreparedStatement statement = Core.getInstance().getSQLConnection().getConnection().prepareStatement(
                                "INSERT INTO `ip_bans` (ip_address) VALUES (?)");
                        statement.setString(1, ip_address);
                        statement.executeUpdate();
                    } catch (Exception e) {}
                    for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                        if(proxiedPlayer.getAddress().getHostString().equalsIgnoreCase(ip_address)) {
                            proxiedPlayer.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&lBan Severity over 9000\n&7You are banned &aforever&7.\n&7Reason: &eIP Banned\n\n&fYou cannot appeal this ban.")));
                            break;
                        }
                    }
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + strings[0] + " &7has been ip banned."));
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + strings[0] + " &7is already ip banned."));
                }
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7Please supply a username/ip address to ip ban."));
        }
    }
}