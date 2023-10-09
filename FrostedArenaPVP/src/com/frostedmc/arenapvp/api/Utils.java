package com.frostedmc.arenapvp.api;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Redraskal_2 on 3/15/2017.
 */
public class Utils {

    public static void UnloadWorld(JavaPlugin plugin, World world, boolean save)
    {
        if (save)
        {
            try
            {
                ((CraftWorld) world).getHandle().save(true, (IProgressUpdate) null);
            }
            catch (ExceptionWorldConflict e)
            {
                e.printStackTrace();
            }

            ((CraftWorld) world).getHandle().saveLevel();
        }

        world.setAutoSave(save);

        CraftServer server = (CraftServer) plugin.getServer();
        CraftWorld craftWorld = (CraftWorld) world;

        Bukkit.getPluginManager().callEvent(new WorldUnloadEvent(((CraftWorld) world).getHandle().getWorld()));

        Iterator<Chunk> chunkIterator = ((CraftWorld) world).getHandle().chunkProviderServer.chunks
                .values().iterator();

        for (Entity entity : world.getEntities())
        {
            entity.remove();
        }

        while (chunkIterator.hasNext())
        {
            net.minecraft.server.v1_8_R3.Chunk chunk = chunkIterator.next();
            chunk.removeEntities();
        }

        ((CraftWorld) world).getHandle().chunkProviderServer.chunks.clear();
        ((CraftWorld) world).getHandle().chunkProviderServer.unloadQueue.clear();

        try
        {
            Field f = server.getClass().getDeclaredField("worlds");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, World> worlds = (java.util.Map<String, World>) f.get(server);
            worlds.remove(world.getName().toLowerCase());
            f.setAccessible(false);
        }
        catch (IllegalAccessException ex)
        {
            System.out.println("Error removing world from bukkit master list: " + ex.getMessage());
        }
        catch (NoSuchFieldException ex)
        {
            System.out.println("Error removing world from bukkit master list: " + ex.getMessage());
        }

        MinecraftServer ms = null;

        try
        {
            Field f = server.getClass().getDeclaredField("console");
            f.setAccessible(true);
            ms = (MinecraftServer) f.get(server);
            f.setAccessible(false);
        }
        catch (IllegalAccessException ex)
        {
            System.out.println("Error getting minecraftserver variable: " + ex.getMessage());
        }
        catch (NoSuchFieldException ex)
        {
            System.out.println("Error getting minecraftserver variable: " + ex.getMessage());
        }

        ms.worlds.remove(ms.worlds.indexOf(craftWorld.getHandle()));
    }

    @SuppressWarnings({ "rawtypes" })
    public static boolean ClearWorldReferences(String worldName)
    {
        HashMap regionfiles = (HashMap) RegionFileCache.a;

        try
        {
            for (Iterator<Object> iterator = regionfiles.entrySet().iterator(); iterator.hasNext();)
            {
                java.util.Map.Entry e = (java.util.Map.Entry) iterator.next();
                RegionFile file = (RegionFile) e.getValue();

                try
                {
                    file.c();
                    iterator.remove();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception while removing world reference for '" + worldName + "'!");
            ex.printStackTrace();
        }

        return true;
    }
}