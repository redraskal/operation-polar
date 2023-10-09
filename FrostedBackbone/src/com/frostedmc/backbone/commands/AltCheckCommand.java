package com.frostedmc.backbone.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;

/**
 * Created by Redraskal_2 on 1/14/2017.
 */
public class AltCheckCommand extends Command {

    public AltCheckCommand() {
        super("altcheck");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if (!Rank.compare(rank, Rank.HELPER)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7You need " + Rank.HELPER.getPrefix(false) + "&7 to execute this action.")
                ));
                return;
            }
        }
        if (strings.length == 1) {
            ResultSet resultSet = Core.getInstance().getAccountManager().queryAccountDetails(strings[0]);
            try {
                if(resultSet.next()) {
                    String ip_address = resultSet.getString("ip");
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + strings[0] + " &7has played on the following accounts:"));
                    ResultSet resultSet2 = Core.getInstance().getAccountManager().queryAccountDetailsAddress(ip_address);
                    while(resultSet2.next()) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &b" + resultSet2.getString("username")));
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + strings[0] + " &7could not be found."));
                }
            } catch (Exception e) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7Something went wrong. Try the command again in a little while."));
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7Please supply a username to check."));
        }
    }
}
