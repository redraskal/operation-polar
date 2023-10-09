package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

/**
 * Created by Redraskal_2 on 1/7/2017.
 */
public class TwoFactorAuthModule extends Module implements PluginMessageListener, Listener {

    private JavaPlugin javaPlugin;
    private List<Player> verifying = new ArrayList<Player>();
    private Map<Player, String> codes = new HashMap<Player, String>();
    private Map<String, Player> requests = new HashMap<String, Player>();

    public TwoFactorAuthModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "TwoFactorAuth";
    }

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.javaPlugin, "chrome-push");
        Bukkit.getMessenger().registerIncomingPluginChannel(this.javaPlugin, "chrome-approve", this);
        Bukkit.getMessenger().registerIncomingPluginChannel(this.javaPlugin, "chrome-deny", this);
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.javaPlugin, "chrome-push");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.javaPlugin, "chrome-approve", this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.javaPlugin, "chrome-deny", this);
        HandlerList.unregisterAll(this);
        verifying.clear();
        codes.clear();
        requests.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(codes.containsKey(event.getPlayer()))
            codes.remove(event.getPlayer());
        if(event.getPlayer().getName().equalsIgnoreCase("redraskal"))
            codes.put(event.getPlayer(), "na91lg");
        if(event.getPlayer().getName().equalsIgnoreCase("CV_"))
            codes.put(event.getPlayer(), "alf932");
        if(event.getPlayer().getName().equalsIgnoreCase("jeremyapps"))
            codes.put(event.getPlayer(), "d83ja9");
        if(codes.containsKey(event.getPlayer())) {
            verifying.add(event.getPlayer());
            String req = UUID.randomUUID().toString().substring(0, 6);
            requests.put(req, event.getPlayer());
            new BukkitRunnable() {
                public void run() {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lYou currently have 2FA enabled on your account. Please follow the instructions on Chrome to continue."));
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF(codes.get(event.getPlayer()) + "," + req
                                + ","+Bukkit.getServerName() + "," + event.getPlayer().getAddress().getHostString());
                        event.getPlayer().sendPluginMessage(javaPlugin, "chrome-push", b.toByteArray());
                        new BukkitRunnable() {
                            int ticks = 0;
                            public void run() {
                                    if(ticks >= (20*30)) {
                                        this.cancel();
                                        if(event.getPlayer().isOnline() && requests.containsKey(req)) {
                                            event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lYour identity could not be verified."));
                                        }
                                    } else {
                                        if(!event.getPlayer().isOnline() || !requests.containsKey(req)) {
                                            this.cancel();
                                        } else {
                                            ticks++;
                                        }
                                    }
                                }
                            }.runTaskTimer(javaPlugin, 0, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(this.javaPlugin, 10L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(verifying.contains(event.getPlayer())) {
            verifying.remove(event.getPlayer());
        }
        if(requests.containsKey(event.getPlayer())) {
            requests.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(verifying.contains(event.getPlayer())) {
            Location newLoc = event.getFrom();
            newLoc.setY(event.getTo().getY());
            newLoc.setYaw(event.getTo().getYaw());
            newLoc.setPitch(event.getTo().getPitch());
            event.setTo(newLoc);
        }
    }

    @EventHandler
    public void onPreProcess(PlayerCommandPreprocessEvent event) {
        if(verifying.contains(event.getPlayer())) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou cannot execute commands without verifying your identity."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreProcess(com.frostedmc.core.events.PlayerChatEvent event) {
        if(verifying.contains(event.getPlayer())) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou cannot send messages without verifying your identity."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(verifying.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(channel.equalsIgnoreCase("chrome-approve")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            try {
                String utf = in.readUTF();
                if(requests.containsKey(utf)) {
                    Player p = requests.get(utf);
                    requests.remove(utf);
                    verifying.remove(p);
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lYou are now able to move, your identity was verified."));
                }
            } catch (IOException e) {}
        }
        if(channel.equalsIgnoreCase("chrome-deny")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            try {
                String utf = in.readUTF();
                if(requests.containsKey(utf)) {
                    Player p = requests.get(utf);
                    requests.remove(utf);
                    verifying.remove(p);
                    p.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lYour identity could not be verified."));
                }
            } catch (IOException e) {}
        }
    }
}