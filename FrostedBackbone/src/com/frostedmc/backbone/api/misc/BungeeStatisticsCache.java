package com.frostedmc.backbone.api.misc;

import com.frostedmc.core.Core;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class BungeeStatisticsCache implements Listener {

    private static BungeeStatisticsCache instance;
    public static BungeeStatisticsCache getInstance() { return instance; }
    public static boolean initialize(Plugin javaPlugin) {
        if(instance != null) return false;
        instance = new BungeeStatisticsCache(javaPlugin);
        return true;
    }

    private Map<UUID, Map<String, Double>> cache = new HashMap<UUID, Map<String, Double>>();

    private BungeeStatisticsCache(Plugin javaPlugin) {
        javaPlugin.getProxy().getPluginManager().registerListener(javaPlugin, this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        if(cache.containsKey(event.getPlayer().getUniqueId()))
            cache.remove(event.getPlayer().getUniqueId());
    }

    public double get(UUID player, String key) {
        if(cache.containsKey(player)) {
            if(cache.get(player).containsKey(key)) return cache.get(player).get(key);
        } else {
            cache.put(player, Core.getInstance()
                    .getStatisticsManager().fetchProfile(player).getArray());
            if(cache.get(player).containsKey(key)) return cache.get(player).get(key);
        }
        return 0;
    }

    public void update(UUID player, String key) {
        cache.get(player).put(key, Core.getInstance()
                .getStatisticsManager().fetchProfile(player).getStatistic(key));
    }
}