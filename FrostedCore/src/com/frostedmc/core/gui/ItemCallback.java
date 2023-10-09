package com.frostedmc.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * Created by Redraskal_2 on 9/2/2016.
 */
public abstract class ItemCallback {

    public abstract void callback(Player player, Action action);
}