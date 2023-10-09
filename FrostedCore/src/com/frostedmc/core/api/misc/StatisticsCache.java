package com.frostedmc.core.api.misc;

import com.frostedmc.core.Core;
import com.frostedmc.core.utils.MessageChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 3/1/2017.
 */
public class StatisticsCache implements Listener {

    private static StatisticsCache instance;
    public static StatisticsCache getInstance() { return instance; }
    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) return false;
        instance = new StatisticsCache(javaPlugin);
        return true;
    }

    private Map<Player, Map<String, Double>> cache = new HashMap<Player, Map<String, Double>>();

    private StatisticsCache(JavaPlugin javaPlugin) {
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(cache.containsKey(event.getPlayer()))
            cache.remove(event.getPlayer());
    }

    public double get(Player player, String key) {
        if(cache.containsKey(player)) {
            if(cache.get(player).containsKey(key)) return cache.get(player).get(key);
        } else {
            cache.put(player, Core.getInstance()
                    .getStatisticsManager().fetchProfile(player.getUniqueId()).getArray());
            if(cache.get(player).containsKey(key)) return cache.get(player).get(key);
        }
        return 0;
    }

    public void update(Player player, String key) {
        cache.get(player).put(key, Core.getInstance()
                .getStatisticsManager().fetchProfile(player.getUniqueId()).getStatistic(key));
        if(MessageChannel.getInstance() != null) {
            MessageChannel.getInstance().OptionsUpdateEvent(player, key);
        }
    }
}