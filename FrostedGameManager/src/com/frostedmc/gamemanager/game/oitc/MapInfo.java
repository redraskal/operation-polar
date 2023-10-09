package com.frostedmc.gamemanager.game.oitc;

import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class MapInfo {

    private java.util.Map<Map, CustomLocation[]> possibleMaps = new HashMap<Map, CustomLocation[]>();

    public MapInfo() {
        possibleMaps.put(new Map("Battlegrounds", new CustomLocation(-82, 76, -430, 168, 27)), new CustomLocation[]{
                new CustomLocation(-36, 56, -435, 89, 0),
                new CustomLocation(-37, 56, -477, 89, 0),
                new CustomLocation(-85, 56, -483, 0, 2),
                new CustomLocation(-134, 56, -454, -89, 2),
                new CustomLocation(-134, 56, -411, -89, 1),
                new CustomLocation(-48, 56, -401, 170, -3),
                new CustomLocation(-73, 62, -445, 91, -2),
                new CustomLocation(-112, 56, -445, -89, 0),
                new CustomLocation(-73, 71, -423, 89, 0),
                new CustomLocation(-108, 72, -445, -89, 0),
                new CustomLocation(-63, 56, -464, 89, 1),
                new CustomLocation(-60, 56, -435, -89, 0)
        });
        this.shuffle();
    }

    public void shuffle() {
        for(Map map : possibleMaps.keySet()) {
            List<CustomLocation> temp = new ArrayList<CustomLocation>();
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