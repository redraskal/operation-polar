package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class BugReportCommand extends Command {

    private boolean enabled = true;
    private Map<Player, Long> lastReport = new HashMap<Player, Long>();

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "bugreport";
    }

    @Override
    public String commandDescription() {
        return "Report an issue with the server";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(enabled) {
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("anticheat") || args[0].equalsIgnoreCase("game") || args[0].equalsIgnoreCase("other")) {
                    if(args.length > 1) {
                        String message = "";
                        for(int i=1; i<args.length; i++) {
                            if(message.isEmpty()) {
                                message = args[i];
                            } else {
                                message+=" " + args[i];
                            }
                        }
                        if(lastReport.containsKey(player)) {
                            if((System.currentTimeMillis() - lastReport.get(player)) >= 60000L) {
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                Core.getInstance().getBugReportManager().createReport(player.getUniqueId(), args[0].toLowerCase(), message);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Thanks for the report! We'll look over it shortly."));
                                lastReport.put(player, System.currentTimeMillis());
                            } else {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7You can't send bug reports that quickly!"));
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                            Core.getInstance().getBugReportManager().createReport(player.getUniqueId(), args[0].toLowerCase(), message);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Thanks for the report! We'll look over it shortly."));
                            lastReport.put(player, System.currentTimeMillis());
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Please specify a message."));
                    }
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Please specify a valid topic. (anticheat/game/other)"));
                }
            } else {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Please specify a topic. (anticheat/game/other)"));
            }
        } else {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7The bug reporting system is currently disabled."));
        }
    }
}