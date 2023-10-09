package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.report.ReportDetails;
import com.frostedmc.core.commands.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Redraskal_2 on 3/12/2017.
 */
public class ViewReportsCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.HELPER;
    }

    @Override
    public String commandLabel() {
        return "viewreports";
    }

    @Override
    public String commandDescription() {
        return "Allows you to view every open report.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length <= 0) {
            sendPage(player, 1);
        } else {
            try {
                int page = Integer.parseInt(args[0]);
                sendPage(player, page);
            } catch (Exception e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Invalid page number."));
            }
        }
    }

    private void sendPage(Player player, int page) {
        Map<Integer, ReportDetails[]> pages = this.getPages();
        if(pages.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7No data to show."));
            return;
        }
        int maxpages = 1;
        for(Integer entry : pages.keySet()) {
            if(entry > maxpages) maxpages = entry;
        }
        if(pages.containsKey(page)) {
            sendHelpMenu(player, page, maxpages, pages.get(page));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Invalid page number."));
        }
    }

    private Map<Integer, ReportDetails[]> getPages() {
        Map<Integer, ReportDetails[]> pages = new HashMap<Integer, ReportDetails[]>();
        ReportDetails[] totalReports = Core.getInstance().getPlayerReportManager().parseDetails();
        if(totalReports.length <= 0) {
            return pages;
        }
        int currentPage = 1;
        List<ReportDetails> current = new ArrayList<ReportDetails>();
        for(int i=0; i<totalReports.length; i++) {
            if(current.size() >= 4 || i == (totalReports.length-1)) {
                current.add(totalReports[i]);
                pages.put(currentPage, current.toArray(new ReportDetails[current.size()]));
                current.clear();
                currentPage++;
            } else {
                current.add(totalReports[i]);
            }
        }
        return pages;
    }

    private void sendHelpMenu(Player player, int page, int maxpages, ReportDetails[] lines) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&b&m------&r &3Reports: Page " + page + "/" + maxpages + " &b&m------"));
        for(ReportDetails line : lines) {
            String username = "unknown";
            try {
                ResultSet resultSet =
                        Core.getInstance().getUUIDFetcher().queryAccountDetails(line.getPlayer());
                if(resultSet.next()) {
                    username = resultSet.getString("username");
                }
            } catch (Exception e) {}
            String reporter = "unknown";
            try {
                ResultSet resultSet =
                        Core.getInstance().getUUIDFetcher().queryAccountDetails(line.getReporter());
                if(resultSet.next()) {
                    reporter = resultSet.getString("username");
                }
            } catch (Exception e) {}
            new FancyMessage(reporter)
                    .color(ChatColor.YELLOW)
                    .tooltip("Left-Click to view.")
                    .command("/viewreport " + reporter)
                    .then(" reporting ")
                    .color(ChatColor.AQUA)
                    .then(username)
                    .color(ChatColor.YELLOW)
                    .tooltip("Left-Click to view.")
                    .command("/viewreport " + reporter + " " + username)
                    .send(player);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&b&m----------------------------"));
    }
}