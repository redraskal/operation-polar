package com.frostedmc.fabrication.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.fabrication.game.GameStatus;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class SkullCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.VIP;
    }

    @Override
    public String commandLabel() {
        return "skull";
    }

    @Override
    public String commandDescription() {
        return "Gives you the skull of any Minecraft player.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(GameStatus.GLOBAL != GameStatus.INGAME
                || GameStatus.JUDGING) {
            player.sendMessage(PredefinedMessage.CANNOT_EXECUTE.build());
            return;
        }

        if(args.length == 1) {
            player.getInventory().addItem(ItemCreator.getInstance().createSkull(1, args[0], "&b&lSkull of " + args[0]));
            player.sendMessage(PredefinedMessage.SKULL_RECIEVED.registerPlaceholder("%username%", args[0]).build());
        } else {
            player.sendMessage(PredefinedMessage.NOT_ENOUGH_ARGUMENTS.registerPlaceholder("%usage%", "/skull <username>").build());
        }
    }
}
