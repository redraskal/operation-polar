package com.frostedmc.backbone.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class GotoCommand extends Command {

    public GotoCommand() {
        super("goto");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                .getRank();
        if(!Rank.compare(rank, Rank.HELPER)) {
            ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                    ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7You need " + Rank.HELPER.getPrefix(false) + "&7 to execute this action.")
            ));
            return;
        }
        if(strings.length <= 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Please supply a username to teleport to."));
            return;
        }
        UUID uuid = Core.getInstance().getUUIDFetcher().parseDetails(strings[0]);
        if(uuid == null) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7We could not locate &e" + strings[0] + "&7."));
        } else {
            if(RedisBungee.getApi().isPlayerOnline(uuid)) {
                ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uuid);
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Network> &7You are now being sent to &e" + serverInfo.getName() + "&7."));
                BungeeCord.getInstance().getPlayer(commandSender.getName())
                        .connect(serverInfo);
            } else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &e" + strings[0] + " &7is currently offline."));
            }
        }
    }
}