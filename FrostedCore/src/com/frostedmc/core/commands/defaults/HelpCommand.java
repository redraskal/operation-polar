package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class HelpCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "help";
    }

    @Override
    public String commandDescription() {
        return "Displays the help menu.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        Rank rank = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank();
        List<Command> availableCommands = new ArrayList<Command>();

        for(Command command : CommandManager.getInstance().getEnabledCommands()) {
            if(Rank.compare(rank, command.requiredRank())) {
                availableCommands.add(command);
            }
        }

        player.sendMessage(PredefinedMessage.LIST_OF_COMMANDS.build());

        for(Command command : availableCommands) {
            if(command.requiredRank().getLevel() > 0) {
                player.sendMessage(PredefinedMessage.COMMAND_IN_LIST.registerPlaceholder("%name%", command.commandLabel())
                        .registerPlaceholder("%rank%", command.requiredRank().getPrefix(false)).build());
            } else {
                player.sendMessage(PredefinedMessage.COMMAND_IN_LIST_PLAYER.registerPlaceholder("%name%", command.commandLabel()).build());
            }

            player.sendMessage(PredefinedMessage.COMMAND_IN_LIST_DESC.registerPlaceholder("%desc%", command.commandDescription()).build());
        }
    }
}