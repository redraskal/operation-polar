package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class ClearChatCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.HELPER;
    }

    @Override
    public String commandLabel() {
        return "cc";
    }

    @Override
    public String commandDescription() {
        return "Clears the chat history of the server.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            for(int i=0; i<200; i++) {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            }
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Server> &7The chat history has been cleared."));
            pl.playSound(player.getLocation(), Sound.FIZZ, 10, 1);
        }
    }
}