package com.frostedmc.arenapvp;

import com.frostedmc.arenapvp.listener.Creative;
import me.redraskal.Glacier.Spectate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 12/23/2016.
 */
public class VelocityUtil {

    public static void flingNearby(Location location, int radius, int power) {
        for(Entity near : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if(near instanceof Player) {
                if(!Spectate.spectators.contains((Player) near)
                        && !Creative.spectators.contains((Player) near)) {
                    fling((Player) near, power);
                }
            }
        }
    }

    public static void fling(final Player ply, int power) {
        double x = -Math.sin(Math.toRadians(ply.getLocation().getYaw())) *
                Math.cos(Math.toRadians(ply.getLocation().getPitch()));
        double z = Math.cos(Math.toRadians(ply.getLocation().getYaw())) *
                Math.cos(Math.toRadians(ply.getLocation().getPitch()));
        double y = -Math.sin(Math.toRadians(ply.getLocation().getPitch()));
        y = y*2;
        ply.setVelocity(new Vector(x, y, z).multiply(-power));
    }
}