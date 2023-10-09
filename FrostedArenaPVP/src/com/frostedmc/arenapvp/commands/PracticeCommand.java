package com.frostedmc.arenapvp.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class PracticeCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.VIP;
    }

    @Override
    public String commandLabel() {
        return "practice";
    }

    @Override
    public String commandDescription() {
        return "Stuck in the queue? Play with a bot instead!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPractice> &7This feature is coming soon."));
    }
}