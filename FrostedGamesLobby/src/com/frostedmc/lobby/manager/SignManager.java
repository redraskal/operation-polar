package com.frostedmc.lobby.manager;

import com.frostedmc.core.Core;
import com.frostedmc.core.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager {

    private static SignManager instance = null;

    public static SignManager getInstance() {
        return instance;
    }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }
        instance = new SignManager(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;
    private List<Block> registeredSigns;
    private Map<String, Sign> mappedSigns = new HashMap<String, Sign>();

    private SignManager(JavaPlugin javaPlugin) {
        Core.getInstance().getLogger().info("[Manager] Enabling Sign Manager...");
        this.javaPlugin = javaPlugin;
        this.registeredSigns = new ArrayList<Block>();
    }

    public void scan(Location location, int radius) {
        Core.getInstance().getLogger().info("[Manager] Scanning for signs...");
        for(Block block : Utils.getBlocksInRadius(location, radius, false)) {
            if(block.getType() == Material.WALL_SIGN) {
                if(!this.registeredSigns.contains(block)) {
                    this.run(block);
                }
            }
        }
    }

    private String isMapped(Sign sign) {
        for(Map.Entry<String, Sign> entry : mappedSigns.entrySet()) {
            if(entry.getValue().getLocation().getBlockX() == sign.getLocation().getBlockX()
                    && entry.getValue().getLocation().getBlockY() == sign.getLocation().getBlockY()
                    && entry.getValue().getLocation().getBlockZ() == sign.getLocation().getBlockZ()) {
                return entry.getKey();
            }
        }
        return "";
    }

    private Sign findExistingSign(String serverInfo) {
        for(Map.Entry<String, Sign> entry : mappedSigns.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(serverInfo.split(",")[0])) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String findExistingSignData(String serverInfo) {
        for(Map.Entry<String, Sign> entry : mappedSigns.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(serverInfo.split(",")[0])) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Block fetchBehind(Sign sign) {
        return sign.getBlock().getRelative(BlockFace.NORTH);
    }

    public void handlePubSub(final String serverInfo) {
        for(Block block : this.registeredSigns) {
            Sign sign = (Sign) block.getState();
            if(sign.getLine(0).contains("[")) {
                String server = sign.getLine(0).replace("[", "").replace("]", "");
                if(server.equalsIgnoreCase(serverInfo.split(",")[0])
                        || server.equalsIgnoreCase("---")) {
                    mappedSigns.put(serverInfo.split(",")[0], sign);
                    doUpdate(sign, serverInfo);
                    return;
                }
            }
        }
    }

    private void doUpdate(Sign sign, String serverInfo) {
        SignState.parse(serverInfo).update(sign, serverInfo);
    }

    public void run(Block sign) {
        if(!this.registeredSigns.contains(sign)) {
            Core.getInstance().getLogger().info("[Manager] Registered sign at: ["
                    + sign.getWorld().getName()
                    + ", " + sign.getLocation().getX() + ", "
                    + sign.getLocation().getY() + ", "
                    + sign.getLocation().getZ() + "]");
            Sign block = (Sign) sign.getState();
            SignState.RESTARTING.updateDefault(block);
            this.registeredSigns.add(sign);
        }
    }
}