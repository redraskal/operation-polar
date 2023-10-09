package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.gui.defaults.OptionsGUI;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 3/1/2017.
 */
public class OptionsCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "options";
    }

    @Override
    public String commandDescription() {
        return "Change global settings for your experience.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        new OptionsGUI(player, CommandManager.getInstance().getPlugin());
    }
}