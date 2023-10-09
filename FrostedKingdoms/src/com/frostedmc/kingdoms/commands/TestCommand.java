package com.frostedmc.kingdoms.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.kingdoms.Kingdoms;
import com.frostedmc.kingdoms.structure.Schematic;
import com.frostedmc.kingdoms.structure.SchematicBuilder;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Redraskal_2 on 11/13/2016.
 */
public class TestCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "test";
    }

    @Override
    public String commandDescription() {
        return "Pastes a test schematic fancily!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length <= 0) {
            player.sendMessage(PredefinedMessage.KINGDOMS_TEST_COMMAND_ERR.build());
        } else {
            File file = new File(Kingdoms.getInstance().getDataFolder().getPath() + "/" + args[0]);
            if(file.exists() && file.getName().endsWith(".schematic")) {
                player.sendMessage(PredefinedMessage.KINGDOMS_TEST_COMMAND.build());
                try {
                    new SchematicBuilder(Kingdoms.getInstance(), Schematic.load(file), player.getLocation(), 0).runTaskLater(Kingdoms.getInstance(), 20L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(PredefinedMessage.KINGDOMS_TEST_COMMAND_ERR.build());
            }
        }
    }
}