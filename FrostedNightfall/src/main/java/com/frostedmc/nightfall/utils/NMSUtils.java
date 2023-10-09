package com.frostedmc.nightfall.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class NMSUtils {

    public static void sendPacket(Player player, Object packet) throws ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Class<?> c_packet = Class.forName(fetchNMSClass("Packet"));
        Class<?> c_playerConnection = Class.forName(fetchNMSClass("PlayerConnection"));

        Object playerConnection = getPlayerConnection(player);

        c_playerConnection.getDeclaredMethod("sendPacket", c_packet).invoke(playerConnection, packet);
    }

    public static void removeEntityClientSide(Player player, Entity entity) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Class<?> c_packetPlayOutEntityDestroy = Class.forName(fetchNMSClass("PacketPlayOutEntityDestroy"));

        Object packetPlayOutEntityDestroy = c_packetPlayOutEntityDestroy.getConstructor(int[].class)
                .newInstance(new int[]{entity.getEntityId()});

        sendPacket(player, packetPlayOutEntityDestroy);
    }

    public static void forceChestState(Block block, boolean open) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Location location = block.getLocation().clone();

        Class<?> c_world = Class.forName(fetchNMSClass("World"));
        Class<?> c_block = Class.forName(fetchNMSClass("Block"));
        Class<?> c_blockPosition = Class.forName(fetchNMSClass("BlockPosition"));
        Class<?> c_tileEntity = Class.forName(fetchNMSClass("TileEntity"));
        Class<?> c_tileEntityChest = Class.forName(fetchNMSClass("TileEntityChest"));
        Class<?> c_tileEntityEnderChest = Class.forName(fetchNMSClass("TileEntityEnderChest"));
        Class<?> c_craftWorld = Class.forName(NMSUtils.fetchBukkitClass("CraftWorld"));

        Method c_getHandle = c_craftWorld.getDeclaredMethod("getHandle");
        Method c_getTileEntity = c_world.getDeclaredMethod("getTileEntity", c_blockPosition);
        Method c_playBlockAction = c_world.getDeclaredMethod("playBlockAction", c_blockPosition,
                c_block, int.class, int.class);
        Method c_w = c_tileEntity.getDeclaredMethod("w");

        Object craftWorld = c_craftWorld.cast(location.getWorld());
        Object worldServer = c_getHandle.invoke(craftWorld);
        Object blockPosition = c_blockPosition.getConstructor(
                double.class, double.class, double.class)
                .newInstance(location.getX(), location.getY(), location.getZ());

        if(block.getType() == Material.CHEST) {
            Object tileEntityChest = c_tileEntityChest.cast(c_getTileEntity
                    .invoke(worldServer, blockPosition));
            c_playBlockAction.invoke(worldServer, blockPosition,
                    c_w.invoke(tileEntityChest), 1, open ? 1 : 0);
        } else {
            Object tileEntityEnderChest = c_tileEntityEnderChest.cast(c_getTileEntity
                    .invoke(worldServer, blockPosition));
            c_playBlockAction.invoke(worldServer, blockPosition,
                    c_w.invoke(tileEntityEnderChest), 1, open ? 1 : 0);
        }
    }

    public static void playSoundEffect(Location location, String soundEffect, float volume, float pitch) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        Class<?> c_packetPlayOutNamedSoundEffect = Class.forName(fetchNMSClass("PacketPlayOutNamedSoundEffect"));

        Object packetPlayOutNamedSoundEffect = c_packetPlayOutNamedSoundEffect.getConstructor(
                String.class, double.class, double.class, double.class, float.class, float.class)
                .newInstance(soundEffect, location.getX(), location.getY(), location.getZ(), volume, pitch);

        location.getWorld().getNearbyEntities(location, 30, 30, 30).forEach(entity -> {
            if(entity instanceof Player) {
                try {
                    sendPacket((Player) entity, packetPlayOutNamedSoundEffect);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sendActionBar(Player player, String text) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        Class<?> c_iChatBaseComponent = Class.forName(fetchNMSClass("IChatBaseComponent"));
        Class<?> c_chatSerializer = Class.forName(fetchNMSClass("IChatBaseComponent$ChatSerializer"));
        Class<?> c_packetPlayOutChat = Class.forName(fetchNMSClass("PacketPlayOutChat"));

        Object chatBaseComponent = c_chatSerializer.getDeclaredMethod("a", String.class).invoke(null,
                "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}");
        Object packetPlayOutChat = c_packetPlayOutChat.getConstructor(c_iChatBaseComponent, byte.class)
                .newInstance(chatBaseComponent, (byte) 2);

        sendPacket(player, packetPlayOutChat);
    }

    public static void sendTitle(Player player, String title, TitleType titleType, int fadeInTime, int showTime, int fadeOutTime)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                InstantiationException, NoSuchFieldException {
        Class<?> c_iChatBaseComponent = Class.forName(fetchNMSClass("IChatBaseComponent"));
        Class<?> c_chatSerializer = Class.forName(fetchNMSClass("IChatBaseComponent$ChatSerializer"));
        Class<?> c_packetPlayOutTitle = Class.forName(fetchNMSClass("PacketPlayOutTitle"));
        Class<?> c_enumTitleAction = Class.forName(fetchNMSClass("PacketPlayOutTitle$EnumTitleAction"));

        Object chatBaseComponent = c_chatSerializer.getDeclaredMethod("a", String.class).invoke(null,
                "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
        Object enumTitleAction = c_enumTitleAction.getDeclaredMethod("a", String.class).invoke(null, titleType.toString());
        Object packetPlayOutTitle = c_packetPlayOutTitle.getConstructor(c_enumTitleAction, c_iChatBaseComponent,
                int.class, int.class, int.class).newInstance(enumTitleAction, chatBaseComponent, fadeInTime, showTime, fadeOutTime);

        sendPacket(player, packetPlayOutTitle);
    }

    public static void sendTitleSubtitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        sendTitle(player, title, TitleType.TITLE, fadeInTime, showTime, fadeOutTime);
        sendTitle(player, subtitle, TitleType.SUBTITLE, fadeInTime, showTime, fadeOutTime);
    }

    public static void clearTitle(Player player) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        Class<?> c_iChatBaseComponent = Class.forName(fetchNMSClass("IChatBaseComponent"));
        Class<?> c_chatSerializer = Class.forName(fetchNMSClass("IChatBaseComponent$ChatSerializer"));
        Class<?> c_packetPlayOutTitle = Class.forName(fetchNMSClass("PacketPlayOutTitle"));
        Class<?> c_enumTitleAction = Class.forName(fetchNMSClass("PacketPlayOutTitle$EnumTitleAction"));

        Object chatBaseComponent = c_chatSerializer.getDeclaredMethod("a", String.class).invoke(null,
                "{\"text\":\"\"}");
        Object enumTitleAction = c_enumTitleAction.getDeclaredMethod("a", String.class).invoke(null, "RESET");
        Object packetPlayOutTitle = c_packetPlayOutTitle.getConstructor(c_enumTitleAction, c_iChatBaseComponent)
                .newInstance(enumTitleAction, chatBaseComponent);

        sendPacket(player, packetPlayOutTitle);
    }

    public static void sendCameraPacket(Player player, int entityID, JavaPlugin javaPlugin) throws ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException, InstantiationException {
        Class<?> c_packetPlayOutCamera = Class.forName(NMSUtils.fetchNMSClass("PacketPlayOutCamera"));

        Field f_a = c_packetPlayOutCamera.getDeclaredField("a");

        Object packetPlayOutCamera = c_packetPlayOutCamera.newInstance();
        f_a.set(packetPlayOutCamera, entityID);

        new BukkitRunnable() {
            public void run() {
                try {
                    sendPacket(player, packetPlayOutCamera);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(javaPlugin, 3L);
    }

    public static Object getPlayerConnection(Player player) throws ClassNotFoundException, NoSuchFieldException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> c_craftPlayer = Class.forName(fetchBukkitClass("entity.CraftPlayer"));
        Class<?> c_entityPlayer = Class.forName(fetchNMSClass("EntityPlayer"));

        Field f_playerConnection = c_entityPlayer.getDeclaredField("playerConnection");

        Object craftPlayer = c_craftPlayer.cast(player);
        Object entityPlayer = c_craftPlayer.getDeclaredMethod("getHandle").invoke(craftPlayer);
        return f_playerConnection.get(entityPlayer);
    }

    public static Object createPacketDataSerializer() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> c_byteBuf = Class.forName("io.netty.buffer.ByteBuf");
        Class<?> c_unpooled = Class.forName("io.netty.buffer.Unpooled");

        Class<?> c_packetDataSerializer = NMSClass.PACKET_DATA_SERIALIZER.fetch();

        Object byteBuf = c_unpooled.getDeclaredMethod("buffer")
                .invoke(null);
        return c_packetDataSerializer.getConstructor(c_byteBuf).newInstance(byteBuf);
    }

    public enum NMSClass {

        PACKET_DATA_SERIALIZER(fetchNMSClass("PacketDataSerializer"));

        @Getter @Setter private final String className;

        private NMSClass(String className) {
            this.className = className;
        }

        public Class<?> fetch() throws ClassNotFoundException {
            return Class.forName(this.className);
        }
    }

    public enum MCVersion {

        V1_12_2(340),
        V1_12_1(338),
        V1_12(335),
        V1_11_2(316),
        V1_11(315),
        V1_10(210),
        V1_9_4(110),
        V1_9_2(109),
        V1_9_1(108),
        V1_9(107),
        V1_8(47),
        V1_7_10(5),
        UNKNOWN(-1);

        @Getter private final int protocol;

        private MCVersion(int protocol) {
            this.protocol = protocol;
        }

        public static MCVersion lookup(int protocol) {
            for(MCVersion mcVersion : MCVersion.values()) {
                if(mcVersion.getProtocol() == protocol) return mcVersion;
            }
            return MCVersion.UNKNOWN;
        }
    }

    public enum TitleType {

        TITLE, SUBTITLE
    }

    public static String fetchNMSVersion() {
        String _package = Bukkit.getServer().getClass().getPackage().getName();
        return _package.substring(_package.lastIndexOf(".") + 1);
    }

    public static String fetchBukkitClass(String _class) {
        return "org.bukkit.craftbukkit." + fetchNMSVersion() + "." + _class;
    }

    public static String fetchNMSClass(String _class) {
        return "net.minecraft.server." + fetchNMSVersion() + "." + _class;
    }
}