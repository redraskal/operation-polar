package com.frostedmc.gamemanager.listener;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 3/1/2017.
 */
public class Move implements Listener {

    public static List<Player> dontAllow = new ArrayList<Player>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(GameManager.getInstance().gameStatus == GameStatus.WAITING && event.getTo().getY() <= 0) {
            event.setTo(GameManager.getInstance().lobbySpawn.clone());
            return;
        }
        if(event.getTo().getWorld().getName()
                .equalsIgnoreCase(event.getFrom().getWorld().getName())) {
            if(dontAllow.contains(event.getPlayer())) {
                if(event.getTo().getX() != event.getFrom().getX()
                        || event.getTo().getZ() != event.getFrom().getZ()) {
                    Location temp = event.getFrom();
                    temp.setY(event.getTo().getY());
                    temp.setYaw(event.getTo().getYaw());
                    temp.setPitch(event.getTo().getPitch());
                    event.setTo(temp);
                }
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        if(GameManager.getInstance().gameStatus.getID() > 1) return;
        if(!event.getTo().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkGenerate(ChunkLoadEvent event) {
        if(event.isNewChunk() && (GameManager.getInstance().gameStatus == GameStatus.WAITING
                || !GameManager.getInstance().getCurrentGame().gameFlags.generateChunks)) {
            event.getChunk().unload(false, true);
        }
    }
}