package com.frostedmc.frostedgames.command;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class ForceGamemakerCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "fgm";
    }

    @Override
    public String commandDescription() {
        return "Force a player to become the gamemaker.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(InternalGameSettings.status == Status.WAITING
                || InternalGameSettings.status == Status.STARTING) {
            if(args.length >= 1) {
                if(Bukkit.getPlayer(args[0]) != null) {
                    InternalGameSettings.gameMaker = Bukkit.getPlayer(args[0]);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + args[0] + " &7is now the gamemaker."));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + args[0] + " &7is not online."));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7Please supply a username."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7The game has already started."));
        }
    }
}