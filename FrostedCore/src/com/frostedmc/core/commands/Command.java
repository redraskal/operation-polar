package com.frostedmc.core.commands;

import com.frostedmc.core.api.account.Rank;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public abstract class Command {

    public abstract Rank requiredRank();

    public abstract String commandLabel();

    public abstract String commandDescription();

    public String[] onTabComplete(Player player, String[] args) {
        return new String[]{};
    }

    public abstract void onCommand(Player player, String[] args);
}