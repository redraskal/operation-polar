package com.frostedmc.kingdoms.backend;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class Utils {

    public static List<Chunk> convertChunks(List<String> stringList) {
        List<Chunk> tempCache = new ArrayList<Chunk>();
        for(String entry : stringList) {
            String[] array = entry.split(",");
            tempCache.add(Bukkit.getWorld(array[0]).getChunkAt(Integer.parseInt(array[1]), Integer.parseInt(array[2])));
        }
        return tempCache;
    }

    public static List<UUID> convertMembers(List<String> stringList) {
        List<UUID> tempCache = new ArrayList<UUID>();
        for(String entry : stringList) {
            tempCache.add(UUID.fromString(entry));
        }
        return tempCache;
    }

    public static int intValue(Object number) {
        if(number instanceof Long) {
            return new Long((long) number).intValue();
        } else {
            return (int) number;
        }
    }
}