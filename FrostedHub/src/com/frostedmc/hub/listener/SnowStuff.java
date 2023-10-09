package com.frostedmc.hub.listener;

import com.frostedmc.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 2/3/2017.
 */
public class SnowStuff extends BukkitRunnable implements Listener {

    public SnowStuff() {
        this.runTaskLater(Hub.getInstance(), 60L);
    }

    public void run() {
        for(int x = -10; x < 10; x++) {
            for(int z = -10; z < 10; z++) {
                replaceChunk(Bukkit.getWorld("world").getChunkAt(x, z), Biome.COLD_TAIGA);
            }
        }
        Bukkit.getWorld("world").setStorm(true);
        Hub.getInstance().getServer().getPluginManager().registerEvents(this, Hub.getInstance());
        new BukkitRunnable() {
            public void run() {
                Hub.allowLogin = true;
            }
        }.runTaskLater(Hub.getInstance(), 20L);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(!event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSnowForm(BlockFormEvent event) {
        if(event.getNewState().getType() == Material.SNOW
                || event.getNewState().getType() == Material.SNOW_BLOCK) {
            event.setCancelled(true);
        }
    }

    private void replaceChunk(Chunk chunk, Biome biome) {
        for(int x=0; x<16; x++) {
            for(int z=0; z<16; z++) {
                chunk.getBlock(x, 0, z).setBiome(biome);
            }
        }
    }
}
