package com.frostedmc.backbone.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Redraskal_2 on 12/31/2016.
 */
public class SpamCommand extends Command {

    public SpamCommand() {
        super("spam");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if(!Rank.compare(rank, Rank.ADMIN)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7You need &c&lADMIN&7 to execute this action.")
                ));
                return;
            }
        }
        if(strings.length <= 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Please supply a username."));
            return;
        }

        this.broadcast(strings[0]);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Sending spam request to Backbone instances..."));
    }

    private void broadcast(String username) {
        RedisBungee.getApi().sendChannelMessage("spam", username);
    }

}
