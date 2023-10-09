package com.frostedmc.gamemanager.listener;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
            event.setCancelled(true);
        }
        if(!GameManager.getInstance().getCurrentGame().gameFlags.allowFoodLevelChange) {
            event.setCancelled(true);
        }
    }
}
