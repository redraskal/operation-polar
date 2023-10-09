package com.frostedmc.lobby.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.lobby.FrostedLobby;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().teleport(FrostedLobby.getInstance().LOBBY_SPAWN);
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        event.getPlayer().setLevel(0);
        event.getPlayer().setExp(0);
        event.getPlayer().setHealthScale(20);
        event.getPlayer().setHealth(20);
        event.getPlayer().setFoodLevel(20);

        JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/AmbientRainforest.mp3");

        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setItem(4, ItemCreator.getInstance().createSkull(1, event.getPlayer().getName(), "&5&lPROFILE"));
        event.getPlayer().getInventory().setItem(0, ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&a&lGAME SELECTOR"));
        event.getPlayer().getInventory().setItem(1, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&2&lLOBBY SELECTOR"));
        event.getPlayer().getInventory().setItem(7, ItemCreator.getInstance().createItem(Material.SLIME_BALL, 1, 0, "&e&lCOSMETICS"));
        event.getPlayer().getInventory().setItem(8, ItemCreator.getInstance().createItem(Material.BREWING_STAND_ITEM, 1, 0, "&6&lNETWORK LEVELING"));
    }
}