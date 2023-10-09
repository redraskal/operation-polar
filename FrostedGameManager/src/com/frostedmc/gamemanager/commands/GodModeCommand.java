package com.frostedmc.gamemanager.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 3/19/2017.
 */
public class GodModeCommand extends Command {

    public static List<String> usernames = new ArrayList<String>();

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "godmode";
    }

    @Override
    public String commandDescription() {
        return "Toggles god mode on a player.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7Please specify a username."));
        } else {
            if(usernames.contains(args[0])) {
                usernames.remove(args[0]);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e"
                        + args[0] + " &7has been removed from the god mode list."));
            } else {
                usernames.add(args[0]);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e"
                        + args[0] + " &7has been added to the god mode list."));
            }
        }
    }
}