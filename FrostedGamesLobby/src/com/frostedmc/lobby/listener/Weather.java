package com.frostedmc.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class Weather implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(event.toWeatherState())
            event.setCancelled(true);
    }
}