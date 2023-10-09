package com.frostedmc.arenapvp.manager;

import com.frostedmc.arenapvp.arena.*;
import com.frostedmc.arenapvp.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class ArenaManager {

    private static ArenaManager instance = null;
    public static ArenaManager getInstance() {
        return instance;
    }
    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }
        instance = new ArenaManager(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;
    private List<Arena> used = new ArrayList<Arena>();
    private Map<Arena, Game> games = new HashMap<Arena, Game>();

    private ArenaManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public void findArena(Queue queue) {
        MapInfo mapInfo = MapInfo.values()[new Random().nextInt(MapInfo.values().length)];
        com.frostedmc.arenapvp.api.Map mapInstance = new com.frostedmc.arenapvp.api.Map
                (mapInfo.getFolder());
        try {
            mapInstance.load();
            queue.end = true;
            Queue.getQueue(queue.rival).end = true;
            createGame(new Arena(mapInfo.getSpawnpointA()
                            .convert(mapInstance.getInstance()),
                            mapInfo.getSpawnpointB()
                                    .convert(mapInstance.getInstance()),
                            mapInfo, mapInstance),
                    queue.kit, queue.player, queue.rival, queue.gameType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createGame(Arena arena, Kit kit, Player player1, Player player2, GameType gameType) {
        if(used.contains(arena))
            return;
        used.add(arena);
        games.put(arena, new Game(player1, player2, arena, kit, gameType));
    }

    public void resetGame(Game game) {
        if(!used.contains(game.getArena()))
            return;
        used.remove(game.getArena());
        games.remove(game.getArena());
        try {
            game.getArena().getMapInstance().unload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Game getGame(Player player) {
        for(Game game : games.values()) {
            if(game.ingame(player)) {
                return game;
            }
        }
        return null;
    }

    public boolean inArena(Player player) {
        for(Game game : games.values()) {
            if(game.ingame(player)) {
                return true;
            }
        }
        return false;
    }

    public int count(Kit kit, GameType gameType) {
        int count = 0;
        for(Game game : games.values()) {
            if(game.getKit() == kit && game.getGameType() == gameType) {
                count++;
            }
        }
        return count;
    }
}