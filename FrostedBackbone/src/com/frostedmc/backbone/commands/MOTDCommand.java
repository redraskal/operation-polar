package com.frostedmc.backbone.commands;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Redraskal_2 on 1/14/2017.
 */
public class MOTDCommand extends Command {

    public MOTDCommand() {
        super("motd");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if (!Rank.compare(rank, Rank.ADMIN)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7You need " + Rank.ADMIN.getPrefix(false) + "&7 to execute this action.")
                ));
                return;
            }
        }
        if (strings.length >= 1) {
            String message = "";
            for(int i=0; i<strings.length; i++) {
                if(message.isEmpty()) {
                    message = strings[i];
                } else {
                    message+=" "+strings[i];
                }
            }
            Backbone.getInstance().getMOTDManager().update(message);
            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7The MOTD has been updated.")));
        } else {
            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Please supply a new motd.")));
        }
    }
}
