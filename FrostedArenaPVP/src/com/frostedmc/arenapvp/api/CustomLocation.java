package com.frostedmc.arenapvp.api;

import org.bukkit.Location;
import org.bukkit.World;

public class CustomLocation {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public CustomLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;

        if(this.x >= 0) {
            this.x+=.5;
        } else {
            this.x-=.5;
        }
        if(this.z >= 0) {
            this.z+=.5;
        } else {
            this.z-=.5;
        }
    }

    public Location convert(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}