package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.module.Module;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class TimeOnlineModule extends Module implements Listener {

    @Override
    public String name() {
        return "Time Online";
    }

    private Map<UUID, Long> joinTimestamp = new HashMap<UUID, Long>();

    @Override
    public void onEnable() {
        Backbone.getInstance().getProxy().getPluginManager().registerListener(Backbone.getInstance(), this);
        Backbone.getInstance().getProxy().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveCache();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @EventHandler
    public void onPreLogin(ServerConnectEvent event) {
        if(!joinTimestamp.containsKey(event.getPlayer().getUniqueId())) {
            joinTimestamp.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(ServerDisconnectEvent event) {
        if(joinTimestamp.containsKey(event.getPlayer().getUniqueId())) {
            saveTime(event.getPlayer().getUniqueId(), joinTimestamp.get(event.getPlayer().getUniqueId()));
            joinTimestamp.remove(event.getPlayer().getUniqueId());
        }
    }

    @Override
    public void onDisable() {
        Backbone.getInstance().getProxy().getPluginManager().unregisterListener(this);
    }

    private void saveCache() {
        Backbone.getInstance().getLogger().info("[TimeCache] Saving online time...");
        Map<UUID, Long> newMap = new HashMap<UUID, Long>();
        for(Map.Entry<UUID, Long> entry : joinTimestamp.entrySet()) {
            this.saveTime(entry.getKey(), entry.getValue());
            newMap.put(entry.getKey(), System.currentTimeMillis());
        }
        joinTimestamp = newMap;
    }

    private void saveTime(UUID uuid, long timeOnline) {
        long total = (System.currentTimeMillis() - timeOnline);
        double minutes = (double) ((int) ((total / 1000) % 60)) / 60;
        Backbone.getInstance().getLogger().info("[TimeCache] Adding " + minutes + " minutes to " + uuid.toString() + "'s record.");
        Core.getInstance().getAccountManager().update(uuid,
                new UpdateDetails(UpdateDetails.UpdateType.TIME_ONLINE,
                        (Core.getInstance().getAccountManager().parseDetails(uuid).getMinutesOnline() + minutes))
        );
    }
}