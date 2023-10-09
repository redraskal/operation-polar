package com.frostedmc.core.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 8/30/2016.
 */
public final class VectorUtils {

    public static final Vector rotateAroundAxisX(Vector paramVector, double paramDouble) {
        double d3 = Math.cos(paramDouble);
        double d4 = Math.sin(paramDouble);
        double d1 = paramVector.getY() * d3 - paramVector.getZ() * d4;
        double d2 = paramVector.getY() * d4 + paramVector.getZ() * d3;
        return paramVector.setY(d1).setZ(d2);
    }

    public static final Vector rotateAroundAxisY(Vector paramVector, double paramDouble) {
        double d3 = Math.cos(paramDouble);
        double d4 = Math.sin(paramDouble);
        double d1 = paramVector.getX() * d3 + paramVector.getZ() * d4;
        double d2 = paramVector.getX() * -d4 + paramVector.getZ() * d3;
        return paramVector.setX(d1).setZ(d2);
    }

    public static final Vector rotateAroundAxisZ(Vector paramVector, double paramDouble) {
        double d3 = Math.cos(paramDouble);
        double d4 = Math.sin(paramDouble);
        double d1 = paramVector.getX() * d3 - paramVector.getY() * d4;
        double d2 = paramVector.getX() * d4 + paramVector.getY() * d3;
        return paramVector.setX(d1).setY(d2);
    }

    public static final Vector rotateVector(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3) {
        rotateAroundAxisX(paramVector, paramDouble1);
        rotateAroundAxisY(paramVector, paramDouble2);
        rotateAroundAxisZ(paramVector, paramDouble3);
        return paramVector;
    }

    public static final double angleToXAxis(Vector paramVector) {
        return Math.atan2(paramVector.getX(), paramVector.getY());
    }

    public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();

        Location loc = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(loc.toVector()); // this sets the returned location's direction toward the center of the circle
        loc.setDirection(difference);

        return loc;
    }
}