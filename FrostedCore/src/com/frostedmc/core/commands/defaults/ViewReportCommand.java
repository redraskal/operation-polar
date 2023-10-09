package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.report.ReportCategory;
import com.frostedmc.core.api.account.report.ReportDetails;
import com.frostedmc.core.commands.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class ViewReportCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.HELPER;
    }

    @Override
    public String commandLabel() {
        return "viewreport";
    }

    @Override
    public String commandDescription() {
        return "Allows you to view reports players create.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Please supply a reporter's username."));
        } else {
            UUID reporter = Core.getInstance().getUUIDFetcher().parseDetails(args[0]);
            if(reporter == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7We could not locate &e" + args[0] + "&7."));
            } else {
                if(args.length == 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Active reports:"));
                    ReportDetails[] reports = Core.getInstance().getPlayerReportManager().parseDetailsBackwards(reporter);
                    if(reports == null || reports.length == 0) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7No data to show."));
                    } else {
                        for(ReportDetails report : reports) {
                            String ign = "unknown";
                            try {
                                ResultSet resultSet =
                                        Core.getInstance().getUUIDFetcher().queryAccountDetails(report.getPlayer());
                                if(resultSet.next()) {
                                    ign = resultSet.getString("username");
                                }
                            } catch (Exception e) {}
                            new FancyMessage("Report> ")
                                    .color(ChatColor.AQUA)
                                    .then(ign)
                                    .color(ChatColor.YELLOW)
                                    .tooltip("Left-Click to view.")
                                    .command("/viewreport " + args[0] + " " + ign)
                                    .send(player);
                        }
                    }
                } else {
                    UUID uuid = Core.getInstance().getUUIDFetcher().parseDetails(args[1]);
                    if(uuid == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7We could not locate &e" + args[1] + "&7."));
                    } else {
                        ReportDetails report = Core.getInstance().getPlayerReportManager()
                                .parseDetails(uuid, reporter);
                        if(report == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Data not found."));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Report details:"));
                            for(Map.Entry<ReportCategory, String> entry : report.getReasons().entrySet()) {
                                String value = entry.getValue();
                                if(value.startsWith(", ")) {
                                    value = value.replaceFirst(", ", "");
                                }
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &e&l"
                                        + entry.getKey().getName() + ": &7" + value));
                            }
                            new FancyMessage("Report> ")
                                    .color(ChatColor.AQUA)
                                    .then("Teleport to player.")
                                    .color(ChatColor.GOLD)
                                    .tooltip("Left-Click to teleport.")
                                    .command("/goto " + args[1])
                                    .send(player);
                        }
                    }
                }
            }
        }
    }
}