package com.frostedmc.gamemanager;

import net.minecraft.server.v1_8_R3.IWorldBorderListener;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 2/27/2017.
 */
public class WorldBorderUtils {

    private WorldBorder worldBorder;
    private Field listeners;
    private IWorldBorderListener listener;

    public WorldBorderUtils(WorldBorder worldBorder) {
        this.worldBorder = worldBorder;
        this.listener = new IWorldBorderListener() {
            @Override
            public void a(WorldBorder worldBorder, double v) {
                sendToAll(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
            }

            @Override
            public void a(WorldBorder worldBorder, double v, double v2, long l) {
                sendToAll(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
            }

            @Override
            public void a(WorldBorder worldBorder, double v, double v2) {
                sendToAll(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
            }

            @Override
            public void a(WorldBorder worldBorder, int i) {
                sendToAll(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME));
            }

            @Override
            public void b(WorldBorder worldBorder, int i) {
                sendToAll(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
            }

            @Override
            public void b(WorldBorder worldBorder, double v) {

            }

            @Override
            public void c(WorldBorder worldBorder, double v) {

            }
        };
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        this.listeners = worldBorder.getClass().getDeclaredField("a");
        this.listeners.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(listeners, listeners.getModifiers() & ~Modifier.FINAL);
        List<IWorldBorderListener> temp = new ArrayList<IWorldBorderListener>();
        temp.add(this.listener);
        this.listeners.set(worldBorder, temp);
    }

    private void sendToAll(Packet packet) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}