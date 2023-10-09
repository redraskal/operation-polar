package com.frostedmc.kingdoms.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.utils.Title;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&5KINGDOMS"),
                ChatColor.translateAlternateColorCodes('&', "&7Welcome back, &e" + event.getPlayer().getName()),
                1, 1, 1);
        title.send(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        if(!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&5News> &7Use &e/k info&7 for information about the gamemode & how to play!"));
            JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.SOUND_EFFECT,
                    "https://downloads.frostedmc.com/music/effects/KingdomsS2NewPlayer.mp3");
            event.getPlayer().teleport(new Location(Bukkit.getWorld("spawn"), 1.5, 115.0, 2.5, 179.8f, 0.8f));
        } else {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&d&m------------------&r &5Kingdoms Season 2 &d&m------------------"));
            event.getPlayer().sendMessage("");
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Use &e/config&7 to view this season's configuration!"));
            event.getPlayer().sendMessage("");
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3&lFORUMS &8&l\u2022 &bwww.frostedmc.com"));
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3&lSTORE &8&l\u2022 &bstore.frostedmc.com"));
            event.getPlayer().sendMessage("");
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Go to &ehttp://music.frostedmc.com&7 for in-game music & sound!"));
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&d&m-----------------------------------------------------"));
            JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.SOUND_EFFECT,
                    "https://downloads.frostedmc.com/music/effects/KingdomsRecurringPlayer.mp3");
            if(event.getPlayer().getWorld().getName().equalsIgnoreCase("spawn")) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld("spawn"), 1.5, 115.0, 2.5, 179.8f, 0.8f));
            }
        }
    }
}