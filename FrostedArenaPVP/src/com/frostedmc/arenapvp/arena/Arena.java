package com.frostedmc.arenapvp.arena;

import com.frostedmc.arenapvp.api.Map;
import org.bukkit.Location;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Arena {

    private Location spawnpointA;
    private Location spawnpointB;
    private Map mapInstance;
    private MapInfo mapInfo;

    public Arena(Location spawnpointA, Location spawnpointB, MapInfo mapInfo, Map mapInstance) {
        this.spawnpointA = spawnpointA;
        this.spawnpointB = spawnpointB;
        this.mapInfo = mapInfo;
        this.mapInstance = mapInstance;
    }

    public Location getSpawnpointA() {
        return this.spawnpointA;
    }

    public Location getSpawnpointB() {
        return this.spawnpointB;
    }

    public MapInfo getMapInfo() {
        return this.mapInfo;
    }

    public Map getMapInstance() {
        return this.mapInstance;
    }
}