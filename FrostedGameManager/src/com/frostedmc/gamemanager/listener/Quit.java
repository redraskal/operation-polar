package com.frostedmc.gamemanager.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.manager.DamageManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Quit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&bLeave> &7" + event.getPlayer().getName()));
        JukeboxManager.getInstance().stopMusic(event.getPlayer().getName());
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            if(!SpectatorMode.getInstance().contains(event.getPlayer())) {
                DamageManager.getInstance().handleDeath(event.getPlayer(), null, "Server Leave");
            }
        }
        new BukkitRunnable() {
            public void run() {
                GameManager.getInstance().publishServer();
            }
        }.runTaskLater(GameManager.getInstance(), 10L);
    }
}