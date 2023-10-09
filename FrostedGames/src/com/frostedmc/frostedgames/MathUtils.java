package com.frostedmc.frostedgames;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Random;

public class MathUtils
{
  private static Random random = new Random();

    public static void fling(final Entity ply, int power) {
        double x = -Math.sin(Math.toRadians(ply.getLocation().getYaw())) *
                Math.cos(Math.toRadians(ply.getLocation().getPitch()));
        double z = Math.cos(Math.toRadians(ply.getLocation().getYaw())) *
                Math.cos(Math.toRadians(ply.getLocation().getPitch()));
        double y = -Math.sin(Math.toRadians(ply.getLocation().getPitch()));
        y = y+3;
        ply.setVelocity(new Vector(x, y, z).multiply(-power));
    }
  
  public static final Vector rotateAroundAxisX(Vector v, double angle)
  {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double y = v.getY() * cos - v.getZ() * sin;
    double z = v.getY() * sin + v.getZ() * cos;
    return v.setY(y).setZ(z);
  }
  
  public static final Vector rotateAroundAxisY(Vector v, double angle)
  {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double x = v.getX() * cos + v.getZ() * sin;
    double z = v.getX() * -sin + v.getZ() * cos;
    return v.setX(x).setZ(z);
  }
  
  public static final Vector rotateAroundAxisZ(Vector v, double angle)
  {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double x = v.getX() * cos - v.getY() * sin;
    double y = v.getX() * sin + v.getY() * cos;
    return v.setX(x).setY(y);
  }
  
  public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ)
  {
    rotateAroundAxisX(v, angleX);
    rotateAroundAxisY(v, angleY);
    rotateAroundAxisZ(v, angleZ);
    return v;
  }
  
  public static final double angleToXAxis(Vector vector)
  {
    return Math.atan2(vector.getX(), vector.getY());
  }
  
  public static Vector getRandomVector()
  {
    double x = random.nextDouble() * 2.0D - 1.0D;
    double y = random.nextDouble() * 2.0D - 1.0D;
    double z = random.nextDouble() * 2.0D - 1.0D;
    
    return new Vector(x, y, z).normalize();
  }
  
  public static void applyVelocity(Entity ent, Vector v)
  {
    ent.setVelocity(v);
  }
  
  public static Vector getRandomCircleVector()
  {
    double rnd = random.nextDouble() * 2.0D * 3.141592653589793D;
    double x = Math.cos(rnd);
    double z = Math.sin(rnd);
    
    return new Vector(x, 0.0D, z);
  }
  
  public static double randomDouble(double min, double max)
  {
    return Math.random() < 0.5D ? (1.0D - Math.random()) * (max - min) + min : Math.random() * (max - min) + min;
  }
  
  public static float randomRangeFloat(float min, float max)
  {
    return (float)(Math.random() < 0.5D ? (1.0D - Math.random()) * (max - min) + min : Math.random() * (max - min) + min);
  }
  
  public static int randomRangeInt(int min, int max)
  {
    return (int)(Math.random() < 0.5D ? (1.0D - Math.random()) * (max - min) + min : Math.random() * (max - min) + min);
  }
  
  public static double offset(Entity a, Entity b)
  {
    return offset(a.getLocation().toVector(), b.getLocation().toVector());
  }
  
  public static double offset(Location a, Location b)
  {
    return offset(a.toVector(), b.toVector());
  }
  
  public static double offset(Vector a, Vector b)
  {
    return a.subtract(b).length();
  }
}
