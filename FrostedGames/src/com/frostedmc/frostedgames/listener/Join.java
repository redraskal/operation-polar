package com.frostedmc.frostedgames.listener;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.SpectatorMode;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.gui.GamemakerInventory;
import com.frostedmc.frostedgames.runnable.StartingRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(Bukkit.getOnlinePlayers().size() >= 25) {
            if(InternalGameSettings.status != Status.INGAME) {
                event.setKickMessage(ChatColor.translateAlternateColorCodes('&', "&cThis server has reached it's maximum capacity for games. Wait until the game starts to join as a spectator."));
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FrostedGames.getInstance().publishServerUpdate();
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&bJoin> &7" + event.getPlayer().getName()));
        if(InternalGameSettings.status == Status.INGAME
                || InternalGameSettings.status == Status.ENDED) {
            SpectatorMode.getInstance().add(event.getPlayer());
            if(InternalGameSettings.deathmatch) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld("deathmatch"), 25.5, 76.0, 32.5, 142.8f, 18.3f));
            } else {
                event.getPlayer().teleport(InternalGameSettings.map.getSpectator());
            }
            event.getPlayer().setFlying(true);
            JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/PVPGameMusic3.mp3");
            return;
        }
        event.getPlayer().teleport(FrostedGames.getInstance().LOBBY_SPAWN);
        resetHotbar(event.getPlayer());
        setWaitingInventory(event.getPlayer());
        InternalGameSettings.rewards.add(event.getPlayer());
        if(Bukkit.getOnlinePlayers().size() >= InternalGameSettings.minPlayers) {
            if(InternalGameSettings.status == Status.WAITING) {
                new StartingRunnable();
            }
        }
    }

    public static void resetHotbar(Player player) {
        player.setHealthScale(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
    }

    public static void setGamemakerInventory(Player player) {
        player.getInventory().clear();
        new GamemakerInventory(player);
    }

    public static void setWaitingInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(3,
                ItemCreator.getInstance().createItem(Material.NAME_TAG, 1, 0, "&e&lTEAM SELECTOR"));
        player.getInventory().setItem(5,
                ItemCreator.getInstance().createItem(Material.EYE_OF_ENDER, 1, 0, "&c&lLEAVE GAME"));
    }
}