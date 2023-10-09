package com.frostedmc.arenapvp.listener;

import com.frostedmc.arenapvp.manager.ArenaManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Damage implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        if(!ArenaManager.getInstance().inArena((org.bukkit.entity.Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
