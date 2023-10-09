package com.frostedmc.fabrication.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Redraskal_2 on 10/22/2016.
 */
public enum Spawnpoint {

    ARENA_1(-140, 263),
    ARENA_2(-23, 263),
    ARENA_3(93, 263),
    ARENA_4(93, 140),
    ARENA_5(-23, 140),
    ARENA_6(-140, 140),
    ARENA_7(-140, 17),
    ARENA_8(-23, 17),
    ARENA_9(93, 17),
    ARENA_10(93, -104),
    ARENA_11(-23, -104),
    ARENA_12(-140, -104),
    ARENA_13(-140, -226),
    ARENA_14(-23, -226),
    ARENA_15(93, -226),
    ARENA_16(93, -347);


    private double x;
    private double z;

    private Spawnpoint(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double fix(double l) {
        if(l >= 0) {
            l+=.5;
        } else {
            l-=.5;
        }
        return l;
    }

    public Location get() {
        return new Location(Bukkit.getWorld("world"), fix(x), 37, fix(z), (float) -179, (float) -2);
    }
}