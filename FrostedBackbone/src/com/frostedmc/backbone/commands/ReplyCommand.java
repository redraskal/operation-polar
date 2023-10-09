package com.frostedmc.backbone.commands;

import com.frostedmc.backbone.api.misc.BungeeStatisticsCache;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", null, new String[]{"r", "respond"});
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        if (strings.length == 0) {
            ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                    ChatColor.translateAlternateColorCodes('&',
                            "&bChat> &7Invalid arguments, try /reply <message...>")
            ));
        } else {
            if (!MessageCommand.lastMessage.containsKey(((ProxiedPlayer) commandSender).getUniqueId())) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&bChat> &7You haven't received or sent a message yet.")
                ));
                return;
            }
            UUID lookup = MessageCommand.lastMessage.get(((ProxiedPlayer) commandSender).getUniqueId());
            String username = RedisBungee.getApi().getNameFromUuid(lookup);
            if (RedisBungee.getApi().isPlayerOnline(lookup)) {
                if (BungeeStatisticsCache.getInstance().get(lookup, "options_pm") == 0) {
                    String message = "";
                    for (int i = 0; i < strings.length; i++) {
                        if (!message.isEmpty()) message += " ";
                        message += strings[i];
                    }
                    ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                            ChatColor.translateAlternateColorCodes('&',
                                    "&b&lYou &3-> &b&l" + username + " &7" + message)
                    ));
                    RedisBungee.getApi().sendChannelMessage("message-command", username + "\n"
                            + commandSender.getName() + "\n" + message);
                    MessageCommand.lastMessage.put(((ProxiedPlayer) commandSender).getUniqueId(), lookup);
                } else {
                    ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                            ChatColor.translateAlternateColorCodes('&',
                                    "&bChat> &e" + username + " &7currently has &ePrivate Messages &7disabled.")
                    ));
                }
            } else {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&bChat> &7We could not locate &e" + username + "&7.")
                ));
            }
        }
    }
}