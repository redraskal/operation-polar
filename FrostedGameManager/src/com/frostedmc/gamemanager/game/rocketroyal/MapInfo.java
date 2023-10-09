package com.frostedmc.gamemanager.game.rocketroyal;

import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Map;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class MapInfo {

    @Getter private java.util.Map<Map, MapLocations> possibleMaps = new HashMap<>();

    public MapInfo() {
        possibleMaps.put(new Map("Wild West", new CustomLocation(-616, 56, 235, 3, 56)),
                new MapLocations(Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-574, 45, 288, 119, 42),
                        new CustomLocation(-652, 45, 244, 114, 23)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-540, 35, 240, 90, 0),
                        new CustomLocation(-588, 6, 279, 90, 0),
                        new CustomLocation(-607, 28, 216, 90, 0),
                        new CustomLocation(-674, 30, 270, 90, 0)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-473, 80, 177, 90, 0),
                        new CustomLocation(-752, 0, 354, 90, 0)
                }), Arrays.asList(new CustomLocation[]{
                        new CustomLocation(-506, 6, 237, 128, -3),
                        new CustomLocation(-602, 35, 311, 154, 10),
                        new CustomLocation(-688, 6, 240, -47, 1),
                        new CustomLocation(-700, 7, 297, -122, 4),
                        new CustomLocation(-666, 28, 319, -127, 5),
                        new CustomLocation(-555, 5, 293, 122, -1),
                        new CustomLocation(-590, 5, 197, -24, 3),
                        new CustomLocation(-645, 17, 212, 36, 13)
                })));
    }

    public static class MapLocations {

        @Getter private final List<CustomLocation> cinematicShot;
        @Getter private final List<CustomLocation> enderCrystals;
        @Getter private final List<CustomLocation> mapBounds;
        @Getter private final List<CustomLocation> spawnpoints;

        public MapLocations(List<CustomLocation> cinematicShot, List<CustomLocation> enderCrystals,
                            List<CustomLocation> mapBounds, List<CustomLocation> spawnpoints) {
            this.cinematicShot = cinematicShot;
            this.enderCrystals = enderCrystals;
            this.mapBounds = mapBounds;
            this.spawnpoints = spawnpoints;
        }

        public void scrambleSpawnPoints() {
            Collections.shuffle(this.spawnpoints);
        }
    }
}