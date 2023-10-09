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
 * Created by Redraskal_2 on 12/30/2016.
 */
public class StaffChatCommand extends Command {

    public StaffChatCommand() {
        super("sc");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if(!Rank.compare(rank, Rank.HELPER)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7You need &3&lHELPER&7 to execute this action.")
                ));
                return;
            }
        }

        if(args.length <= 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Please supply a message."));
            return;
        }

        String msg = "";
        for(int i=0; i< args.length; i++) {
            if(msg.isEmpty()) {
                msg = args[i];
            } else {
                msg = msg + " " + args[i];
            }
        }

        this.broadcast((ProxiedPlayer) commandSender, msg);
    }

    private void broadcast(ProxiedPlayer proxiedPlayer, String message) {
        RedisBungee.getApi().sendChannelMessage("staffchat", proxiedPlayer.getUniqueId().toString() + "\n" + proxiedPlayer.getName() + "\n" + message);
    }
}