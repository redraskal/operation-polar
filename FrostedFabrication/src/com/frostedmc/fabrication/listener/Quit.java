package com.frostedmc.fabrication.listener;

import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Redraskal_2 on 10/22/2016.
 */
public class Quit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        playerQuitEvent.setQuitMessage(PredefinedMessage.LEAVE
                .registerPlaceholder("%username%", playerQuitEvent.getPlayer().getName())
                .build());
    }
}