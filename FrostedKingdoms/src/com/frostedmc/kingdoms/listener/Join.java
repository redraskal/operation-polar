package com.frostedmc.kingdoms.listener;

import com.frostedmc.kingdoms.Hotbar;
import com.frostedmc.kingdoms.Kingdoms;
import com.frostedmc.kingdoms.backend.PlayerDataManager;
import com.frostedmc.kingdoms.title.TitleAnimator;
import com.frostedmc.kingdoms.title.defaults.SlideRightAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 11/15/2016.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(null);
        if(PlayerDataManager.getInstance().fetchPlayerData(playerJoinEvent.getPlayer().getUniqueId()).inKingdom()) {
            Hotbar.LOBBY_DATA.apply(playerJoinEvent.getPlayer());
        } else {
            Hotbar.LOBBY_NO_DATA.apply(playerJoinEvent.getPlayer());
        }
        Kingdoms.reset(playerJoinEvent.getPlayer());
        playerJoinEvent.getPlayer().teleport(Kingdoms.getInstance().SPAWN_LOCATION);
        new BukkitRunnable() {
            public void run() {
                new TitleAnimator(playerJoinEvent.getPlayer(), new SlideRightAnimation(
                        ChatColor.GRAY + "Welcome to " + ChatColor.DARK_PURPLE + "Kingdoms,",
                        ChatColor.YELLOW + playerJoinEvent.getPlayer().getName(),
                        1, true, Sound.ORB_PICKUP, Sound.CLICK, Sound.BLAZE_DEATH));
            }
        }.runTaskLater(Kingdoms.getInstance(), 20L);
    }
}