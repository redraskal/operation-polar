package com.frostedmc.gamemanager;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public class Utils {

    public static void shootRandomFirework(Location startLocation) {
        Firework firework = startLocation.getWorld().spawn(startLocation, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)))
                .withFade(Color.WHITE)
                .flicker(true)
                .trail(true)
                .build());
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(GameManager.getInstance(), 3L);
    }

    public static void shootFirework(Location startLocation, FireworkEffect fireworkEffect) {
        Firework firework = startLocation.getWorld().spawn(startLocation, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(fireworkEffect);
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(GameManager.getInstance(), 3L);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static String shortenName(String username) {
        if(username.length() <= 9) return username;
        String temp = username.substring(0, 9);
        temp+="...";
        return temp;
    }

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

        Iterator<net.minecraft.server.v1_8_R3.Chunk> chunkIterator = ((CraftWorld) world).getHandle().chunkProviderServer.chunks
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
            Map<String, World> worlds = (Map<String, World>) f.get(server);
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
                Map.Entry e = (Map.Entry) iterator.next();
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