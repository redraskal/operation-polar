package com.frostedmc.arenapvp.listener;

import com.frostedmc.arenapvp.manager.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Hunger implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!ArenaManager.getInstance().inArena((org.bukkit.entity.Player) event.getEntity())) {
            event.setFoodLevel(20);
        }
    }
}