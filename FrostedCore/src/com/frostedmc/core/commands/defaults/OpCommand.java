package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class OpCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "op";
    }

    @Override
    public String commandDescription() {
        return "Ops yourself (from the console).";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(PredefinedMessage.NOW_OPPED_COMMAND.registerPlaceholder("%username%", player.getName()).build());
    }
}