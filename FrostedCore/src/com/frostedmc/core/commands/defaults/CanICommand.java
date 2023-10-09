package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class CanICommand extends Command {
    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "cani";
    }

    @Override
    public String commandDescription() {
        return "Can you?";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(PredefinedMessage.CAN_I_COMMAND.build());
    }
}