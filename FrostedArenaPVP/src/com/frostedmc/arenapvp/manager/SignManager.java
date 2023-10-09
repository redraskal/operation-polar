package com.frostedmc.arenapvp.manager;

import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.core.Core;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
    private Map<Kit, Sign> mappedSigns = new HashMap<Kit, Sign>();

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

    public String[] fetchTop(Kit kit) {
        List<String> lines = new ArrayList<String>();
        try {
            PreparedStatement preparedStatement =
                    Core.getInstance().getSQLConnection().getConnection().prepareStatement(
                            "SELECT * FROM `arena_elo` WHERE kit=? ORDER BY value DESC LIMIT 1");
            preparedStatement.setString(1, kit.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                lines.add(kit.getName() + " Elo:");
                lines.add(Core.getInstance().getAccountManager().parseDetails(
                        UUID.fromString(resultSet.getString("uuid"))).getUsername());
                lines.add(resultSet.getInt("value") + " elo");
                lines.add("Ranked #1");
            }
        } catch (Exception e) {}
        if(lines.isEmpty()) {
            lines.add(kit.getName() + " Elo:");
            lines.add("---");
            lines.add("--- elo");
            lines.add("Ranked #1");
        }
        return lines.toArray(new String[lines.size()]);
    }

    public void run(Block sign) {
        if(!this.registeredSigns.contains(sign)) {
            Core.getInstance().getLogger().info("[Manager] Registered sign at: ["
                    + sign.getWorld().getName()
                    + ", " + sign.getLocation().getX() + ", "
                    + sign.getLocation().getY() + ", "
                    + sign.getLocation().getZ() + "]");
            this.registeredSigns.add(sign);
            if(mappedSigns.size() < Kit.values().length) {
                Kit kit = Kit.values()[mappedSigns.size()];
                Sign block = (Sign) sign.getState();
                mappedSigns.put(kit, (Sign) block);
                new BukkitRunnable() {
                    public void run() {
                        String[] lines = fetchTop(kit);
                        for(int i=0; i<lines.length; i++) {
                            block.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
                        }
                        if(block.getWorld().getPlayers().size() > 0) {
                            try {
                                block.update();
                            } catch (Exception e) {}
                        }
                    }
                }.runTaskTimer(javaPlugin, 0, (20*30));
                return;
            }
            Sign block = (Sign) sign.getState();
            new BukkitRunnable() {
                int currentDot = 0;
                boolean dir = true;
                public void run() {
                    block.setLine(1, ChatColor.translateAlternateColorCodes('&', "Loading"));

                    String dots = "";
                    for(int i=0; i<3; i++) {
                        if(i == currentDot) {
                            dots+="0";
                        } else {
                            dots+="o";
                        }
                    }
                    if(dir) {
                        currentDot++;
                    } else {
                        currentDot--;
                    }
                    if(currentDot >= 2 || currentDot <= 0) {
                        dir = !dir;
                    }

                    block.setLine(2, ChatColor.translateAlternateColorCodes('&', dots));
                    if(block.getWorld().getPlayers().size() > 0) {
                        try {
                            block.update();
                        } catch (Exception e) {}
                    }
                }
            }.runTaskTimer(javaPlugin, 0, 10L);
        }
    }
}