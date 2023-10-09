package com.frostedmc.nightfall.command;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.nightfall.Nightfall;
import com.frostedmc.nightfall.structure.Schematic;
import com.frostedmc.nightfall.structure.SchematicBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SchematicTestCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "schemtest";
    }

    @Override
    public String commandDescription() {
        return "Test the Schematic Builder.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length > 0) {
            File schematicFile = new File("/home/nightfall/schematics/" + args[0] + ".schematic");
            if(schematicFile.exists()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bNightfall> &7Pasting schematic..."));
                try {
                    Schematic schematic = Schematic.load(schematicFile);
                    SchematicBuilder schematicBuilder = new SchematicBuilder(
                            Nightfall.getPlugin(Nightfall.class),
                            schematic, player.getLocation(), 0);
                    schematicBuilder.runTaskTimer(Nightfall.getPlugin(Nightfall.class), 0, 60L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bNightfall> &cThe specified schematic does not exist."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bNightfall> &cPlease provide a schematic name."));
        }
    }
}