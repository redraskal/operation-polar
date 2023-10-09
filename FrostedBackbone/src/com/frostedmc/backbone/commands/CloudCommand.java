package com.frostedmc.backbone.commands;

import com.frostedmc.backbone.automate.ServerInfo;
import com.frostedmc.backbone.automate.ServerManager;
import com.frostedmc.backbone.automate.TemplateInfo;
import com.frostedmc.backbone.automate.TemplateManager;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class CloudCommand extends Command {

    public static long last = System.currentTimeMillis();

    public CloudCommand() {
        super("cloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        last = System.currentTimeMillis();
        if(commandSender instanceof ProxiedPlayer) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(((ProxiedPlayer) commandSender).getUniqueId())
                    .getRank();

            if(!Rank.compare(rank, Rank.ADMIN)) {
                ((ProxiedPlayer) commandSender).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7You need &c&lADMIN&7 to execute this action.")
                ));
                return;
            }
        }
        if(strings.length <= 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Please specify a server cloud action."));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/cloud add <server>"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/cloud remove <server/-a>"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/cloud restart <server> (-s)"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/cloud rca <server/template/-a>"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/cloud refresh"));
            return;
        }
        if(!strings[0].equalsIgnoreCase("add")
                && !strings[0].equalsIgnoreCase("remove")
                && !strings[0].equalsIgnoreCase("restart")
                && !strings[0].equalsIgnoreCase("rca")
                && !strings[0].equalsIgnoreCase("refresh")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Invalid sub-command. Type /cloud for help."));
            return;
        }
        if(strings[0].equalsIgnoreCase("add")) {
            if(strings.length == 1) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Not enough arguments! Try /cloud add <server>"));
                return;
            }
            if(!strings[1].contains("-") || strings[1].split("-")[1].isEmpty()) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Invalid server name. Try Template-Number"));
                return;
            }
            if(ServerManager.getInstance().doesServerExist(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that server already exists."));
                return;
            }
            if(TemplateManager.getInstance().getTemplate(strings[1].split("-")[0]) == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that template doesn't exist."));
                return;
            }
            ServerInfo serverInfo = ServerManager.getInstance().createServer(TemplateManager.getInstance().getTemplate(strings[1].split("-")[0]), strings[1]);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Cloud> &e" + strings[1] + " &7is now being created. You should be able to connect to it within 10 seconds."));
            ServerManager.getInstance().startServer(serverInfo);
        }
        if(strings[0].equalsIgnoreCase("remove")) {
            if(strings.length == 1) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Not enough arguments! Try /cloud remove <server>"));
                return;
            }
            if(strings[1].equalsIgnoreCase("-a")) {
                for(ServerInfo serverInfo : ServerManager.getInstance().getServers(true)) {
                    ServerManager.getInstance().stopServer(serverInfo);
                }
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Cloud> &eEvery server &7is now being removed."));
            } else {
                if(!ServerManager.getInstance().doesServerExist(strings[1])) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that server doesn't exist."));
                    return;
                }
                if(ServerManager.getInstance().getServer(strings[1]).isStarted()) {
                    ServerManager.getInstance().stopServer(ServerManager.getInstance().getServer(strings[1]));
                    ServerManager.getInstance().deleteServer(ServerManager.getInstance().getServer(strings[1]));
                } else {
                    ServerManager.getInstance().deleteServer(ServerManager.getInstance().getServer(strings[1]));
                }
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Cloud> &e" + strings[1] + " &7is now being removed."));
            }
        }
        if(strings[0].equalsIgnoreCase("restart")) {
            if(strings.length == 1) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Not enough arguments! Try /cloud restart <server>"));
                return;
            }
            if(!ServerManager.getInstance().doesServerExist(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that server doesn't exist."));
                return;
            }
            if(ServerManager.getInstance().getServer(strings[1]).isStarted()) {
                ServerManager.getInstance().stopServer(ServerManager.getInstance().getServer(strings[1]));
            }
            if(strings.length > 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Cloud> &e" + strings[1] + " &7is now being stopped."));
            } else {
                ServerManager.getInstance().startServer(ServerManager.getInstance().getServer(strings[1]));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Cloud> &e" + strings[1] + " &7is now being restarted."));
            }
        }
        if(strings[0].equalsIgnoreCase("rca")) {
            if(strings.length == 1) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Not enough arguments! Try /cloud rca <server/template/-a>"));
                return;
            }
            if(strings[1].contains("-") && !strings[1].equalsIgnoreCase("-a")) {
                if(!ServerManager.getInstance().doesServerExist(strings[1])) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that server doesn't exist."));
                    return;
                }
                if(ServerManager.getInstance().getServer(strings[1]).isStarted()) {
                    ServerManager.getInstance().stopServer(ServerManager.getInstance().getServer(strings[1]));
                }
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Cloud> &e" + strings[1] + " &7is now being updated."));
                ServerManager.getInstance().deleteServer(ServerManager.getInstance().getServer(strings[1]));
                ServerInfo serverInfo = ServerManager.getInstance().createServer(TemplateManager.getInstance().getTemplate(strings[1].split("-")[0]), strings[1]);
                ServerManager.getInstance().startServer(serverInfo);
            } else {
                if(strings[1].equalsIgnoreCase("-a")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Cloud> &eEvery server &7is now being updated."));
                    for(TemplateInfo templateInfo : TemplateManager.getInstance().getTemplates()) {
                        for(ServerInfo serverInfo : ServerManager.getInstance().getServers(templateInfo)) {
                            if(serverInfo.isStarted()) {
                                ServerManager.getInstance().stopServer(serverInfo);
                            }
                            ServerManager.getInstance().deleteServer(serverInfo);
                            ServerInfo serverInfoNew =
                                    ServerManager.getInstance().createServer(TemplateManager.getInstance().getTemplate(
                                            serverInfo.getServerName().split("-")[0]), serverInfo.getServerName());
                            ServerManager.getInstance().startServer(serverInfoNew);
                        }
                    }
                } else {
                    if(TemplateManager.getInstance().getTemplate(strings[1]) == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cloud> &7Oh noes, that template doesn't exist."));
                        return;
                    }
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6Cloud> &e" + strings[1] + " servers &7are now being updated."));
                    for(ServerInfo serverInfo : ServerManager.getInstance().getServers(TemplateManager.getInstance().getTemplate(strings[1]))) {
                        if(serverInfo.isStarted()) {
                            ServerManager.getInstance().stopServer(serverInfo);
                        }
                        ServerManager.getInstance().deleteServer(serverInfo);
                        ServerInfo serverInfoNew =
                                ServerManager.getInstance().createServer(TemplateManager.getInstance().getTemplate(
                                        serverInfo.getServerName().split("-")[0]), serverInfo.getServerName());
                        ServerManager.getInstance().startServer(serverInfoNew);
                    }
                }
            }
        }
        if(strings[0].equalsIgnoreCase("refresh")) {
            TemplateManager.getInstance().refreshTemplates();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Cloud> &7The template cache has been refreshed."));
        }
    }
}