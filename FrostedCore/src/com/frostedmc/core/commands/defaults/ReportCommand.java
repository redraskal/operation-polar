package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.defaults.PlayerReportGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class ReportCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "report";
    }

    @Override
    public String commandDescription() {
        return "Report a player for various reasons.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReport> &7Please supply a username."));
        } else {
            new PlayerReportGUI(player, args[0], CommandManager.getInstance().getPlugin());
        }
    }
}