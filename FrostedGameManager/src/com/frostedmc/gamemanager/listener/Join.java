package com.frostedmc.gamemanager.listener;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.runnable.StartingRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(GameManager.getInstance().getCurrentGame() == null) return;
        if(Bukkit.getOnlinePlayers().size() >= GameManager.getInstance().getCurrentGame().gameFlags.maxPlayers) {
            if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
                event.setKickMessage(ChatColor.translateAlternateColorCodes('&', "&cThis server has reached it's maximum capacity for games. Wait until the game starts to join as a spectator."));
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&bJoin> &7" + event.getPlayer().getName()));
        GameManager.getInstance().publishServer();
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME
                || GameManager.getInstance().gameStatus == GameStatus.ENDED) {
            SpectatorMode.getInstance().add(event.getPlayer());
            GameManager.getInstance().getCurrentGame().onSpectatorJoin(event.getPlayer());
            event.getPlayer().setFlying(true);
            return;
        }
        event.getPlayer().teleport(GameManager.getInstance().lobbySpawn);
        resetHotbar(event.getPlayer());
        setWaitingInventory(event.getPlayer());
        if(GameManager.getInstance().getCurrentGame() == null) return;
        GameManager.getInstance().getCurrentGame().onPlayerJoinLobby(event.getPlayer());
        if(Bukkit.getOnlinePlayers().size() >= GameManager.getInstance().getCurrentGame().gameFlags.minPlayers) {
            if(GameManager.getInstance().gameStatus == GameStatus.WAITING) {
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

    public static void setWaitingInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(8,
                ItemCreator.getInstance().createItem(Material.EYE_OF_ENDER, 1, 0, "&c&lLEAVE GAME"));
    }
}