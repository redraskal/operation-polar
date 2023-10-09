package com.frostedmc.core.glacier;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 8/31/2016.
 */
public abstract class MoveCheck {

    public abstract void on(Player player, Location from, Location to);
}
