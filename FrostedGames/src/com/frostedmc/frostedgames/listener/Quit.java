package com.frostedmc.frostedgames.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.CustomDamage;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class Quit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&bLeave> &7" + event.getPlayer().getName()));
        JukeboxManager.getInstance().stopMusic(event.getPlayer().getName());
        new BukkitRunnable() {
            public void run() {
                FrostedGames.getInstance().publishServerUpdate();
            }
        }.runTaskLater(FrostedGames.getInstance(), 10L);
        if(InternalGameSettings.status == Status.INGAME) {
            if(InternalGameSettings.districtMap.containsKey(event.getPlayer())) {
                CustomDamage.handleDeath(event.getPlayer(), null, "Server Leave");
            }
        }
    }
}