package com.frostedmc.gamemanager.game.aerial;

import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Map;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class MapInfo {

    private java.util.Map<Map, MapLocations> possibleMaps = new HashMap<>();

    public MapInfo() {
        possibleMaps.put(new Map("Testing Grounds", new CustomLocation(10, 97, 26, 164, 34)),
                new MapLocations(Arrays.asList(new CustomLocation[]{
                        new CustomLocation(70, 106, -13, 79, 90),
                        new CustomLocation(-55, 106, 9, 79, 90)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-15, 74, -22, 90, 0),
                        new CustomLocation(23, 74, -15, 90, 0),
                        new CustomLocation(17, 74, 22, 90, 0),
                        new CustomLocation(-22, 74, 16, 90, 0)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(11, 68, -10, 90, 0),
                        new CustomLocation(-10, 68, 11, 90, 0)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-99, 120, 100, 90, 0),
                        new CustomLocation(100, 50, -99, 90, 0)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(71, 98, 8, 100, 13),
                        new CustomLocation(71, 98, -11, 85, 14),
                        new CustomLocation(69, 97, -1, 95, 14),
                        new CustomLocation(69, 97, -3, 89, 11)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-65, 94, -3, -77, 15),
                        new CustomLocation(-65, 94, 14, -97, 15),
                        new CustomLocation(-63, 93, 4, -92, 15),
                        new CustomLocation(-63, 93, 6, -95, 14)
                })));
    }

    public java.util.Map<Map, MapLocations> getPossibleMaps() {
        return this.possibleMaps;
    }

    public static class MapLocations {

        @Getter private final List<CustomLocation> cinematicShot;
        @Getter private final List<CustomLocation> enderCrystals;
        @Getter private final List<CustomLocation> centerBounds;
        @Getter private final List<CustomLocation> mapBounds;
        @Getter private final List<CustomLocation> teamASpawnpoints;
        @Getter private final List<CustomLocation> teamBSpawnpoints;

        public MapLocations(List<CustomLocation> cinematicShot, List<CustomLocation> enderCrystals,
                            List<CustomLocation> centerBounds, List<CustomLocation> mapBounds,
                            List<CustomLocation> teamASpawnpoints, List<CustomLocation> teamBSpawnpoints) {
            this.cinematicShot = cinematicShot;
            this.enderCrystals = enderCrystals;
            this.centerBounds = centerBounds;
            this.mapBounds = mapBounds;
            this.teamASpawnpoints = teamASpawnpoints;
            this.teamBSpawnpoints = teamBSpawnpoints;
        }
    }
}
