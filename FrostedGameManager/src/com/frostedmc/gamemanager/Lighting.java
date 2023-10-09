package com.frostedmc.gamemanager;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.EnumSkyBlock;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 3/18/2017.
 */
public class Lighting {

    public static void createLight(Location loc, int level) {
        loc.setPitch(0.0F);
        loc.setYaw(0.0F);
        loc.setX(loc.getBlockX());
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ());
        CraftWorld world = (CraftWorld)loc.getWorld();
        int oldLevel = loc.getBlock().getLightLevel();
        BlockPosition position = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        world.getHandle().a(EnumSkyBlock.BLOCK, position, level);
        updateChunk(loc);
        world.getHandle().a(EnumSkyBlock.BLOCK, position, oldLevel);
        updateChunk(loc);
    }

    public static void removeLight(Location loc) {
        int id = loc.getBlock().getType().getId();
        loc.getBlock().setTypeId(id == 1 ? 2 : 1);
        loc.getBlock().setTypeId(id);
        updateChunk(loc);
    }

    private static void updateChunk(Location loc) {
        for (Player p : loc.getWorld().getPlayers()) {
            List<Chunk> list = new ArrayList();
            list.add(((CraftChunk)loc.getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(16.0D, 0.0D, 0.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(16.0D, 0.0D, 16.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(16.0D, 0.0D, -16.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(-16.0D, 0.0D, 0.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(-16.0D, 0.0D, 16.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(-16.0D, 0.0D, -16.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(0.0D, 0.0D, 16.0D).getChunk()).getHandle());
            list.add(((CraftChunk)loc.add(0.0D, 0.0D, -16.0D).getChunk()).getHandle());
            PacketPlayOutMapChunkBulk packet = new PacketPlayOutMapChunkBulk(list);
            int t = loc.clone().add(0.0D, 1.0D, 0.0D).getBlock().getTypeId();
            loc.clone().add(0.0D, 1.0D, 0.0D).getBlock().setTypeId(t == 1 ? 2 : 1);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            loc.clone().add(0.0D, 1.0D, 0.0D).getBlock().setTypeId(t);
        }
    }
}