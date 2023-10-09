package com.frostedmc.core.utils;

import com.frostedmc.core.Core;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/28/2017.
 */
public class SkinChanger {

    public static boolean change(Player player, String username, JavaPlugin javaPlugin) {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        UUID uuid = Core.getInstance().getUUIDFetcher().parseDetails(username);
        if(uuid == null) return false;
        String properties = Core.getInstance().getGameProfileFetcher().parseDetails(uuid);
        if(properties == null || properties.isEmpty()) return false;
        try {
            // Name -> Id
            craftPlayer.getProfile().getProperties().removeAll("textures");
            craftPlayer.getProfile().getProperties().put("textures",
                    new Property("textures", properties));
            HashMap<Integer, ItemStack> stacks = new HashMap<>();
            for (ItemStack is : player.getInventory().getContents()) {
                if (is == null) continue;
                stacks.put(player.getInventory().first(is), is);
            }
            PlayerInfo playerInfo =
                    new PlayerInfo(player.getHealth(), player.getFoodLevel(),
                            player.getLocation().clone().add(0, 1, 0), stacks);
            sendPackets(new PacketPlayOutEntityDestroy(craftPlayer.getEntityId()));
            sendPackets(new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));
            player.getInventory().clear();
            player.setHealth(0D);
            new BukkitRunnable() {
                public void run() {
                    player.spigot().respawn();
                    player.setHealth(playerInfo.getHealth());
                    player.setFoodLevel(playerInfo.getFood());
                    player.teleport(playerInfo.getLoc());

                    for (ItemStack is : playerInfo.getItems().values()) {
                        player.getInventory().setItem(getKeyByValue(playerInfo.getItems(), is), is);
                    }

                    sendPackets(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
                    sendPacketsNotFor(player.getName(), new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
                }
            }.runTaskLater(javaPlugin, 2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void sendPackets(Packet... packets) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Packet packet : packets) {
                EntityPlayer nmsPlayer = ((CraftPlayer) p).getHandle();

                nmsPlayer.playerConnection.sendPacket(packet);
            }
        }
    }

    private static void sendPacketsNotFor(String notFor, Packet... packets) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getName().equals(notFor)) {
                for (Packet packet : packets) {
                    EntityPlayer nmsPlayer = ((CraftPlayer) p).getHandle();

                    nmsPlayer.playerConnection.sendPacket(packet);
                }
            }
        }
    }

    public static class PlayerInfo {

        private double health;
        private int food;
        private Location loc;
        private HashMap<Integer, ItemStack> items = new HashMap<>();

        public PlayerInfo(double health, int food, Location loc, HashMap<Integer, ItemStack> items) {
            this.health = health;
            this.food = food;
            this.loc = loc;
            this.items = items;
        }

        public double getHealth() {
            return health;
        }

        public int getFood() {
            return food;
        }

        public Location getLoc() {
            return loc;
        }

        public HashMap<Integer, ItemStack> getItems() {
            return items;
        }
    }
}