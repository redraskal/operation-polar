package com.frostedmc.fabrication.listener;

import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.fabrication.game.Countdown;
import com.frostedmc.fabrication.game.GameStatus;
import com.frostedmc.fabrication.game.Spawnpoint;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 10/22/2016.
 */
public class Join implements Listener {

    @EventHandler
    public void asyncPlayerJoin(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        if(GameStatus.GLOBAL == GameStatus.INGAME) {
            asyncPlayerPreLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "This game has already been started.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(PredefinedMessage.JOIN
                .registerPlaceholder("%username%", playerJoinEvent.getPlayer().getName())
                .build());

        reset(playerJoinEvent.getPlayer());
        if(Bukkit.getOnlinePlayers().size() >= GameStatus.MIN_PLAYERS) { new Countdown(); }
    }

    public static void reset(Player player) {
        player.teleport(Spawnpoint.ARENA_1.get().add(0, 1, 0));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
    }
}