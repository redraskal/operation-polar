package com.frostedmc.gamemanager.game.borderbusters;

import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class MapInfo {

    private java.util.Map<Map, CustomLocation[]> possibleMaps = new HashMap<>();

    public MapInfo() {
        possibleMaps.put(new Map("Test", new CustomLocation(-12, 28, -4, 178, 4)), new CustomLocation[]{
                new CustomLocation(-12, 22, -4, 178, 4),
                new CustomLocation(2, 22, -22, 39, 4),
                new CustomLocation(-11, 22, -18, -3, 3),
                new CustomLocation(-23, 22, -2, -110, 7),
                new CustomLocation(-14, 22, 4, -174, 1),
                new CustomLocation(0, 22, 5, 148, 3)
        });
        this.shuffle();
    }

    public void shuffle() {
        for(Map map : possibleMaps.keySet()) {
            List<CustomLocation> temp = new ArrayList<>();
            for(CustomLocation value : possibleMaps.get(map)) {
                temp.add(value);
            }
            Collections.shuffle(temp);
            possibleMaps.remove(map);
            possibleMaps.put(map, temp.toArray(new CustomLocation[temp.size()]));
        }
    }

    public java.util.Map<Map, CustomLocation[]> getPossibleMaps() {
        return this.possibleMaps;
    }
}
