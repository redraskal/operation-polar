package com.frostedmc.arenapvp.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Quit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        JukeboxManager.getInstance().stopMusic(event.getPlayer().getName());
    }
}