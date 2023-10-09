package com.frostedmc.hub.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.hub.Hub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 2/2/2017.
 */
public class Quit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        JukeboxManager.getInstance().stopMusic(event.getPlayer().getName());
        new BukkitRunnable() {
            public void run() {
                Hub.getInstance().publishServer();
            }
        }.runTaskLater(Hub.getInstance(), 10L);
    }
}