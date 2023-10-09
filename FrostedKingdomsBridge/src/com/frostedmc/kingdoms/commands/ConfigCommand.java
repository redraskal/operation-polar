package com.frostedmc.kingdoms.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class ConfigCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "config";
    }

    @Override
    public String commandDescription() {
        return "View the current Kingdoms configuration.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.STRIKETHROUGH + "-------- "
                + ChatColor.DARK_PURPLE + "Frosted Kingdoms Season 2 Config"
                + ChatColor.LIGHT_PURPLE.toString() + ChatColor.STRIKETHROUGH + "-------- ");
        player.sendMessage(ChatColor.YELLOW + "Modifiers ->");
        player.sendMessage(ChatColor.GRAY + "     CutClean");
        player.sendMessage(ChatColor.GRAY + "     Custom Enchants" + ChatColor.RED + " [RARE]");
        player.sendMessage(ChatColor.GRAY + "     Levels");
        player.sendMessage(ChatColor.GRAY + "     Custom Bosses");
        player.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------");
        player.sendMessage(ChatColor.GOLD + "OP-Level -> " + ChatColor.GRAY + "Normal");
        player.sendMessage(ChatColor.YELLOW + "Worlds ->");
        player.sendMessage(ChatColor.GRAY + "     Overworld: " + ChatColor.GREEN + "ENABLED" + ChatColor.GRAY
                + " \u2503 " + ChatColor.YELLOW + "Border - 7.5k");
        player.sendMessage(ChatColor.GRAY + "     Nether: " + ChatColor.GREEN + "ENABLED" + ChatColor.GRAY
                + " \u2503 " + ChatColor.YELLOW + "Border - 1.5k");
        player.sendMessage(ChatColor.GRAY + "     End: " + ChatColor.RED + "DISABLED");
    }
}