package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class CloseReportCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.HELPER;
    }

    @Override
    public String commandLabel() {
        return "closereport";
    }

    @Override
    public String commandDescription() {
        return "Closes a report against a player.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length <= 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Invalid arguments, try /closereport <reporter> <username>."));
        } else {
            UUID reporter = Core.getInstance().getUUIDFetcher().parseDetails(args[0]);
            if(reporter == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7We could not locate &e" + args[0] + "&7."));
                return;
            }
            UUID uuid = Core.getInstance().getUUIDFetcher().parseDetails(args[1]);
            if(uuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7We could not locate &e" + args[1] + "&7."));
                return;
            }
            if(Core.getInstance().getPlayerReportManager().parseDetails(uuid, reporter) == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7We could not find an open report."));
            } else {
                Core.getInstance().getPlayerReportManager().deleteReport(uuid, reporter);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7The specified report has been closed."));
                Utils.pushSlackMessage("xoxb-153245903298-pfYka3OsJTyYqFy2SJLw8Mji",
                        "C4H760HNW", "*" + player.getName() + "* closed a report created by _" + args[0] + "_ regarding _" + args[1] + "_.");
            }
        }
    }
}