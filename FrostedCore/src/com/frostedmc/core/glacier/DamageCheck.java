package com.frostedmc.core.glacier;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Redraskal_2 on 9/2/2016.
 */
public abstract class DamageCheck {

    public abstract void on(Player player, Entity entity, EntityDamageEvent.DamageCause cause);
}