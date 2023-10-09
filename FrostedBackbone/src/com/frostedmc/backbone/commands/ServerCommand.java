package com.frostedmc.backbone.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class ServerCommand extends Command {

    public ServerCommand() {
        super("server");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ConsoleCommandSender) return;
        if(args.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Network> &7You are currently connected to &e"
                    + BungeeCord.getInstance().getPlayer(commandSender.getName()).getServer().getInfo().getName() + "&7."));
        } else {
            if(ProxyServer.getInstance().getServerInfo(args[0]) != null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Network> &7You are now being sent to &e"
                        + args[0] + "&7."));
                BungeeCord.getInstance().getPlayer(commandSender.getName()).connect(ProxyServer.getInstance().getServerInfo(args[0]));
            } else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Network> &e" + args[0] + " &7does not exist."));
            }
        }
    }
}
