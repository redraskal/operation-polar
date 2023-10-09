package com.nametag.plugin.packets;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class PacketAccessor {

    static Field MEMBERS;
    static Field PREFIX;
    static Field SUFFIX;
    static Field TEAM_NAME;
    static Field PARAM_INT;
    static Field PACK_OPTION;
    static Field DISPLAY_NAME;

    private static Class<?> packetClass;

    static {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String className = version.startsWith("v1_5") ? "Packet209SetScoreboardTeam" : "PacketPlayOutScoreboardTeam";
            packetClass = Class.forName("net.minecraft.server." + version + "." + className);

            PREFIX = getNMS(version.startsWith("v1_8") ? "c" : "c");
            SUFFIX = getNMS(version.startsWith("v1_8") ? "d" : "d");
            MEMBERS = getNMS(version.startsWith("v1_8") ? "g" : version.startsWith("v1_9") ? "h" : "e");
            TEAM_NAME = getNMS(version.startsWith("v1_8") ? "a" : "a");
            PARAM_INT = getNMS(version.startsWith("v1_8") ? "h" : version.startsWith("v1_9") ? "i" : "f");
            PACK_OPTION = getNMS(version.startsWith("v1_8") ? "i" : version.startsWith("v1_9") ? "j" : "g");
            DISPLAY_NAME = getNMS(version.startsWith("v1_8") ? "b" : version.startsWith("v1_9") ? "b" : "b");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Field getNMS(String path) throws Exception {
        Field field = packetClass.getDeclaredField(path);
        field.setAccessible(true);
        return field;
    }

    public static Object createPacket() {
        try {
            return packetClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}