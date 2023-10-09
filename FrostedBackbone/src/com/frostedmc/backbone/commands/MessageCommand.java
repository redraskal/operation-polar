package com.frostedmc.backbone.commands;

import com.frostedmc.backbone.api.misc.BungeeStatisticsCache;
import com.frostedmc.core.Core;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class MessageCommand extends Command {

    public static Map<UUID, UUID> lastMessage = new HashMap<UUID, UUID>();

    public MessageCommand() {
        super("msg", null, new String[]{"tell", "m", "message"});
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        if(strings.length <= 1) {
            ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                    ChatColor.translateAlternateColorCodes('&',
                            "&bChat> &7Invalid arguments, try /msg <username> <message...>")
            ));
        } else {
            if(strings[0].equalsIgnoreCase(commandSender.getName())) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&bChat> &7You cannot message yourself.")
                ));
                return;
            }
            UUID lookup = Core.getInstance().getUUIDFetcher().parseDetails(strings[0]);
            if(lookup == null) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&bChat> &7We could not locate &e" + strings[0] + "&7.")
                ));
                return;
            }
            if(RedisBungee.getApi().isPlayerOnline(lookup)) {
                if(BungeeStatisticsCache.getInstance().get(lookup, "options_pm") == 0) {
                    String message = "";
                    for(int i=1; i<strings.length; i++) {
                        if(!message.isEmpty()) message+=" ";
                        message+=strings[i];
                    }
                    ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                            ChatColor.translateAlternateColorCodes('&',
                                    "&b&lYou &3-> &b&l" + strings[0] + " &7" + message)
                    ));
                    RedisBungee.getApi().sendChannelMessage("message-command", strings[0] + "\n"
                            + commandSender.getName() + "\n" + message);
                    lastMessage.put(((ProxiedPlayer) commandSender).getUniqueId(), lookup);
                } else {
                    ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                            ChatColor.translateAlternateColorCodes('&',
                                    "&bChat> &e" + strings[0] + " &7currently has &ePrivate Messages &7disabled.")
                    ));
                }
            } else {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&bChat> &7We could not locate &e" + strings[0] + "&7.")
                ));
            }
        }
    }
}