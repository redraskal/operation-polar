package com.frostedmc.gamemanager.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public abstract class Game implements Listener {

    public abstract String gameName();
    public GameFlags gameFlags = new GameFlags();
    public Random random = new Random();
    private List<Map> loadedMaps = new ArrayList<>();
    private final UUID gameUUID = UUID.randomUUID();

    public abstract void onGameLoad();

    public abstract void onGameUnload();

    public abstract void onMapLoad(Map map);

    public abstract void onMapUnload(Map map);

    public abstract void onGameStart();

    public abstract void onPlayerJoinLobby(Player player);

    public abstract void onItemClick(Player player, ItemStack itemStack, int slot);

    public abstract void onSpectatorJoin(Player player);

    public abstract void onGameEnd();

    public List<Map> getLoadedMaps() {
        return this.loadedMaps;
    }

    public UUID getGameUUID() {
        return this.gameUUID;
    }

    public boolean loadMap(Map map) {
        if(this.loadedMaps.contains(map)) return false;
        if(!map.load()) return false;
        this.loadedMaps.add(map);
        this.onMapLoad(map);
        return true;
    }

    public boolean unloadMap(Map map) {
        if(!this.loadedMaps.contains(map)) return false;
        if(!map.unload()) return false;
        this.loadedMaps.remove(map);
        this.onMapUnload(map);
        return true;
    }

    public boolean unloadMaps() {
        for(Map map : this.loadedMaps) {
            if(!map.unload()) return false;
            this.onMapUnload(map);
        }
        this.loadedMaps.clear();
        return true;
    }
}