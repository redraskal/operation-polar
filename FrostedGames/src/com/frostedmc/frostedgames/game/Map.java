package com.frostedmc.frostedgames.game;

import com.frostedmc.frostedgames.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public enum Map {

    DOWNSTREAM("Downstream", new CustomLocation(0, 130, 0, 5, 5), new CustomLocation[]{
            new CustomLocation(-17, 115, -2, -81, 11),
            new CustomLocation(-17, 115, 2, -97, 14),
            new CustomLocation(-16, 115, 6, -110, 8),
            new CustomLocation(-14, 115, 10, -124, 10),
            new CustomLocation(-10, 115, 14, -142, 10),
            new CustomLocation(-6, 115, 16, -158, 10),
            new CustomLocation(-2, 115, 16, -171, 14),
            new CustomLocation(2, 115, 17, 172, 13),
            new CustomLocation(6, 115, 16, 158, 13),
            new CustomLocation(10, 115, 14, 144, 12),
            new CustomLocation(14, 115, 10, 127, 12),
            new CustomLocation(16, 115, 6, 112, 13),
            new CustomLocation(17, 115, 2, 99, 13),
            new CustomLocation(17, 115, -2, 82, 15),
            new CustomLocation(16, 115, -6, 69, 14),
            new CustomLocation(14, 115, -10, 54, 14),
            new CustomLocation(10, 115, -14, 37, 10),
            new CustomLocation(6, 115, -16, 24, 12),
            new CustomLocation(2, 115, -17, 9, 15),
            new CustomLocation(-2, 115, -17, -8, 14),
            new CustomLocation(-6, 115, -16, -21, 15),
            new CustomLocation(-10, 115, -14, -34, 14),
            new CustomLocation(-14, 115, -10, -52, 14),
            new CustomLocation(-16, 115, -6, -66, 13)
    }),
    ARCTIC("Arctic", new CustomLocation(6, 35, 16, 160, 31), new CustomLocation[]{
            new CustomLocation(0, 17, 25, -179, 0),
            new CustomLocation(-5, 17, 22, -164, 0),
            new CustomLocation(-10, 17, 19, -149, 0),
            new CustomLocation(-14, 17, 15, -136, 0),
            new CustomLocation(-18, 17, 11, -120, 0),
            new CustomLocation(-21, 17, 6, -108, 0),
            new CustomLocation(-24, 17, 0, -89, 0),
            new CustomLocation(-21, 17, -5, -75, 0),
            new CustomLocation(-18, 17, -10, -61, 0),
            new CustomLocation(-14, 17, -14, -45, 0),
            new CustomLocation(-10, 17, -18, -30, 0),
            new CustomLocation(-5, 17, -21, -16, 0),
            new CustomLocation(0, 17, -24, 0, 0),
            new CustomLocation(6, 17, -21, 15, 0),
            new CustomLocation(11, 17, -18, 30, 0),
            new CustomLocation(15, 17, -15, 43, 0),
            new CustomLocation(19, 17, -11, 58, 0),
            new CustomLocation(22, 17, -5, 75, 0),
            new CustomLocation(25, 17, 0, 90, 0),
            new CustomLocation(22, 17, 6, 105, 0),
            new CustomLocation(19, 17, 11, 119, 0),
            new CustomLocation(15, 17, 15, 134, 0),
            new CustomLocation(11, 17, 19, 149, 0),
            new CustomLocation(6, 17, 22, 150, 0),
    });
    /**
     * ELCAST("Elcast", new CustomLocation(-1013, 23, 282, -102, 30), new CustomLocation[]{
     new CustomLocation(-1023, 15, 277, -89, 4),
     new CustomLocation(-1022, 15, 272, -73, 5),
     new CustomLocation(-1019, 15, 267, -54, 3),
     new CustomLocation(-1015, 15, 263, -34, 4),
     new CustomLocation(-1010, 15, 260, -17, 6),
     new CustomLocation(-1005, 15, 259, 0, 4),
     new CustomLocation(-1000, 15, 260, 17, 5),
     new CustomLocation(-995, 15, 263, 36, 3),
     new CustomLocation(-991, 15, 267, 53, 6),
     new CustomLocation(-988, 15, 272, 75, 4),
     new CustomLocation(-987, 15, 277, 90, 1),
     new CustomLocation(-988, 15, 282, 106, 5),
     new CustomLocation(-991, 15, 287, 128, 4),
     new CustomLocation(-995, 15, 291, 143, 4),
     new CustomLocation(-1000, 15, 294, 163, 3),
     new CustomLocation(-1005, 15, 295, -180, 3),
     new CustomLocation(-1010, 15, 294, -163, 5),
     new CustomLocation(-1015, 15, 291, -145, 3),
     new CustomLocation(-1019, 15, 287, -126, 4),
     new CustomLocation(-1022, 15, 282, -106, 5)
     });
     */

    private String name;
    private CustomLocation spectator;
    private CustomLocation[] spawnpoints;

    private Map(String name, CustomLocation spectator, CustomLocation[] spawnpoints) {
        this.name = name;
        this.spectator = spectator;
        this.spawnpoints = spawnpoints;
    }

    public String getName() {
        return this.name;
    }

    public Location getSpectator() {
        return this.spectator.convert(Bukkit.getWorld("game"));
    }

    public Location[] getSpawnpoints() {
        List<Location> temp = new ArrayList<Location>();
        for(CustomLocation customLocation : spawnpoints) {
            temp.add(customLocation.convert(Bukkit.getWorld("game")));
        }
        return temp.toArray(new Location[temp.size()]);
    }
}