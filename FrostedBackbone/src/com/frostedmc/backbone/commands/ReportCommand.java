package com.frostedmc.backbone.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class ReportCommand extends Command {

    public ReportCommand() {
        super("report");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            if (args.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Please supply who you are reporting and a reason for it."));
                return;
            }

            boolean isOnline = false;
            for(ProxiedPlayer proxiedPlayer : ((ProxiedPlayer) commandSender).getServer().getInfo().getPlayers()) {
                if(proxiedPlayer.getName().equalsIgnoreCase(args[0])) {
                    isOnline = true;
                }
            }
            if(!isOnline) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &e" + args[0] + " &7is not online."));
                return;
            }

            if(args[0].equalsIgnoreCase(commandSender.getName())) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Backbone> &7Don't try to report yourself! D:"));
                return;
            }

            String reason = "";
            for (int i = 1; i < args.length; i++) {
                if (reason.isEmpty()) {
                    reason = args[i];
                } else {
                    reason = reason + " " + args[i];
                }
            }

            this.broadcast((ProxiedPlayer) commandSender, args[0], reason);
        }
    }

    private void broadcast(ProxiedPlayer proxiedPlayer, String reporting, String reason) {
        RedisBungee.getApi().sendChannelMessage("report", proxiedPlayer.getName() + "\n" + proxiedPlayer.getServer().getInfo().getName() + "\n"
                + reporting + "\n" + reason);
    }
}