package com.frostedmc.core.utils;

import com.frostedmc.core.Core;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.ssl.HttpsURLConnection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 9/2/2016.
 */
public class Utils {

    public static void pushSlackMessage(String token, String channel, String message) {
        Core.getInstance().getLogger().info("[Slack] Sending request to server...");
        message = message.replace(" ", "%20");
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://slack.com/api/chat.postMessage?token=" + token + "&channel=" + channel + "&as_user=true&text="
                    + message);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            Core.getInstance().getLogger().info("[Slack] " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonElement fetchJSON(String url) throws Exception {
        URL connection = new URL(url);
        HttpsURLConnection request = (HttpsURLConnection) connection.openConnection();
        request.setRequestMethod("GET");
        request.setRequestProperty("User-Agent", "Minecraft");
        request.connect();
        JsonParser parser = new JsonParser();
        return parser.parse(new InputStreamReader((InputStream) request.getContent()));
    }

    public static List<String> convert(String[] stringArray) {
        List<String> result = new ArrayList<String>();

        for(String line : stringArray) {
            result.add(line);
        }

        return result;
    }

    public static String[] convertColors(String[] oldArray) {
        List<String> result = new ArrayList<String>();

        for(String line : oldArray) {
            result.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return result.toArray(new String[result.size()]);
    }

    public static double convertToSeconds(long startTime) {
        return Math.round(((System.currentTimeMillis() - startTime) / 1000));
    }

    public static String secondsLeft(int total, double seconds) {
        return new DecimalFormat("##.##").format((total - seconds));
    }

    public static boolean cantStandAtWater(Block block) {
        Block otherBlock = block.getRelative(BlockFace.DOWN);

        boolean isHover = block.getType() == Material.AIR;
        boolean n = (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.STATIONARY_WATER);
        boolean s = (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.STATIONARY_WATER);
        boolean e = (otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.EAST).getType() == Material.STATIONARY_WATER);
        boolean w = (otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.WEST).getType() == Material.STATIONARY_WATER);
        boolean ne = (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.STATIONARY_WATER);
        boolean nw = (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.STATIONARY_WATER);
        boolean se = (otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.STATIONARY_WATER);
        boolean sw = (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER) || (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.STATIONARY_WATER);

        return (n) && (s) && (e) && (w) && (ne) && (nw) && (se) && (sw) && (isHover);
    }

    public static boolean compareLocations(Location currentLocation, Location previousLocation) {
        return (currentLocation.getX() == previousLocation.getX() && currentLocation.getZ() == previousLocation.getZ());
    }

    public static boolean isHoveringOverWater(Location player, int blocks) {
        for (int i = player.getBlockY(); i > player.getBlockY() - blocks; i--) {
            Block newloc = new Location(player.getWorld(), player.getBlockX(), i, player.getBlockZ()).getBlock();

            if (newloc.getType() != Material.AIR) {
                return newloc.isLiquid();
            }
        }

        return false;
    }

    public static boolean isHoveringOverWater(Location player) {
        return isHoveringOverWater(player, 25);
    }

    public static boolean isFullyInWater(Location player) {
        double touchedX = fixXAxis(player.getX());
        return (new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ()).getBlock().isLiquid()) && (new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()).getBlock().isLiquid());
    }

    public static boolean isInWater(Player player) {
        Material m = player.getLocation().getBlock().getType();

        if ((m == Material.STATIONARY_WATER) || (m == Material.WATER)) {
            return true;
        }

        return false;
    }

    public static boolean isInWeb(Player player) {
        return (player.getLocation().getBlock().getType() == Material.WEB) || (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEB) || (player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WEB);
    }

    public static double fixXAxis(double x) {
        double touchedX = x;
        double rem = touchedX - Math.round(touchedX) + 0.01D;
        if (rem < 0.3D) {
            touchedX = NumberConversions.floor(x) - 1;
        }
        return touchedX;
    }

    public static boolean blocksNear(Player player)
    {
        return blocksNear(player.getLocation());
    }

    public static boolean blocksNear(Location loc) {
        boolean nearBlocks = false;

        for (Block block : getSurrounding(loc.getBlock(), true)) {
            if (block.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }

        for (Block block : getSurrounding(loc.getBlock(), false)) {
            if (block.getType() != Material.AIR)
            {
                nearBlocks = true;
                break;
            }
        }

        Location a = loc;a.setY(a.getY() - 0.5D);
        if (a.getBlock().getType() != Material.AIR) {
            nearBlocks = true;
        }

        if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER })) {
            nearBlocks = true;
        }

        return nearBlocks;
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();

        int bX = location.getBlockX();
        int bY = location.getBlockY();
        int bZ = location.getBlockZ();

        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {

                    double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));

                    if (distance < radius * radius
                            && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != Material.BARRIER)
                            blocks.add(l.getBlock());
                    }
                }

            }
        }

        return blocks;
    }

    public static void refreshChunks(Location location, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                location.getWorld().refreshChunk((location.getChunk().getX()+x), (location.getChunk().getZ()+z));
            }
        }
    }

    public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
        ArrayList<Block> blocks = new ArrayList();

        if (diagonals) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if ((x != 0) || (y != 0) || (z != 0)) {
                            blocks.add(block.getRelative(x, y, z));
                        }
                    }
                }
            }
        } else {
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        }

        return blocks;
    }

    public static boolean isBlock(Block block, Material[] materials) {
        Material type = block.getType();
        Material[] arrayOfMaterial;
        int j = (arrayOfMaterial = materials).length;

        for (int i = 0; i < j; i++) {
            Material m = arrayOfMaterial[i];

            if (m == type) {
                return true;
            }
        }

        return false;
    }

    public static String add(String string, String addTo) {
        if(string.isEmpty()) {
            return addTo;
        } else {
            return string + ", " + addTo;
        }
    }

    public static ItemStack removeDefaultLores(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = craftItemStack.getTag();

        if(nbt == null) {
            nbt = new NBTTagCompound();
        }

        nbt.setInt("HideFlags", 63);
        craftItemStack.setTag(nbt);

        return CraftItemStack.asCraftMirror(craftItemStack);
    }

    public static float huePicker(float hue) {
        if (hue >= 1.0F) {
            hue = 0.0F;
        }
        hue = (float)(hue + 0.01F);

        return hue;
    }

    public static List<Color> rainbowRGB() {
        List<Color> colors = new ArrayList<Color>();
        for (int r=0; r<100; r++) colors.add(new Color(r*255/100,       255,         0));
        for (int g=100; g>0; g--) colors.add(new Color(      255, g*255/100,         0));
        for (int b=0; b<100; b++) colors.add(new Color(      255,         0, b*255/100));
        for (int r=100; r>0; r--) colors.add(new Color(r*255/100,         0,       255));
        for (int g=0; g<100; g++) colors.add(new Color(        0, g*255/100,       255));
        for (int b=100; b>0; b--) colors.add(new Color(        0,       255, b*255/100));
        colors.add(new Color(        0,       255,         0));

        return colors;
    }

    public static float[] getRGB(float hue) {
        int argb = Color.HSBtoRGB(hue / 20.0F, 1.0F, 1.0F);
        float r = (argb >> 16 & 0xFF) / 255.0F;
        float g = (argb >> 8 & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        r = r == 0.0F ? 0.001F : r;

        return new float[]{r, g, b};
    }

    public static int[] separateRGB(int rgb) {
        int blue = rgb & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int red = (rgb >> 16) & 0xFF;

        return new int[]{red, green, blue};
    }

    public static void sendCustomTab(Player player, String header, String footer) {
        IChatBaseComponent chatBaseComponentA = IChatBaseComponent
                .ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', header) + "\"}");
        IChatBaseComponent chatBaseComponentB = IChatBaseComponent
                .ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', footer) + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field field = packet.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.set(packet, chatBaseComponentA);
            field.setAccessible(!field.isAccessible());
            Field field2 = packet.getClass().getDeclaredField("b");
            field2.setAccessible(true);
            field2.set(packet, chatBaseComponentB);
            field2.setAccessible(!field.isAccessible());
        } catch (Exception e) {}
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void explodeRocket(Firework firework, JavaPlugin javaPlugin) {
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(javaPlugin, 3L);
    }
}