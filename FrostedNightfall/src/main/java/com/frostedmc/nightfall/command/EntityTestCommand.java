package com.frostedmc.nightfall.command;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.nightfall.Nightfall;
import com.frostedmc.nightfall.entity.IlluminatiEntity;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class EntityTestCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "entitytest";
    }

    @Override
    public String commandDescription() {
        return "idk?";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        new IlluminatiEntity(player.getLocation().clone(), Nightfall.getPlugin(Nightfall.class));
    }
}