package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class WebsiteCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "website";
    }

    @Override
    public String commandDescription() {
        return "View the link to our website!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        new FancyMessage("Server> ")
                .color(ChatColor.AQUA)
                .then("You can visit our website at: ")
                .color(ChatColor.GRAY)
                .then("https://frostedmc.com")
                .tooltip("Click here to visit the website!")
                .link("https://frostedmc.com")
                .send(player);
    }
}