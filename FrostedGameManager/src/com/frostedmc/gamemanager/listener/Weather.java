package com.frostedmc.gamemanager.listener;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Weather implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
            if(event.toWeatherState()) event.setCancelled(true);
            return;
        }
        if(!GameManager.getInstance().getCurrentGame().gameFlags.allowWeatherChange) {
            event.setCancelled(true);
        }
    }
}
