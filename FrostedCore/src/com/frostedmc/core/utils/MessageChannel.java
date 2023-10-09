package com.frostedmc.core.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class MessageChannel {

    private static MessageChannel instance;
    public static MessageChannel getInstance() { return instance; }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null)
            return false;
        instance = new MessageChannel(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;

    private MessageChannel(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "custom-kick");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "custom-message");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "custom-rca");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "custom-switch");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "options-update");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "report-notify");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "glacier-notify");
        javaPlugin.getServer().getMessenger().registerOutgoingPluginChannel(javaPlugin, "BungeeCord");
    }

    public void OptionsUpdateEvent(Player sender, String option) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(sender.getName() + "," + option);
            sender.sendPluginMessage(this.javaPlugin, "options-update", b.toByteArray());
        } catch (Exception e) {}
    }

    public void ReportCreate(Player sender, UUID uuid) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(sender.getName() + "," + uuid.toString());
            sender.sendPluginMessage(this.javaPlugin, "report-notify", b.toByteArray());
        } catch (Exception e) {}
    }

    public void GlacierNotification(Player sender, String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(message);
            sender.sendPluginMessage(this.javaPlugin, "glacier-notify", b.toByteArray());
        } catch (Exception e) {}
    }

    public void send(Player sender, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            sender.sendPluginMessage(this.javaPlugin, "BungeeCord", b.toByteArray());
        } catch (Exception e) {}
    }

    public void Switch(Player sender, String username, String prefix) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(username);
            out.writeUTF(prefix);
            sender.sendPluginMessage(this.javaPlugin, "custom-switch", b.toByteArray());
        } catch (Exception e) {}
    }

    public void remoteRCA(Player sender, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(server);
            sender.sendPluginMessage(this.javaPlugin, "custom-rca", b.toByteArray());
        } catch (Exception e) {}
    }

    public void kickPlayerGlobally(Player sender, String toKick, String reason) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(toKick + ":" + reason);
            sender.sendPluginMessage(this.javaPlugin, "custom-kick", b.toByteArray());
        } catch (Exception e) {}
    }

    public void sendMessage(Player sender, String username, String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(username + ":" + message);
            sender.sendPluginMessage(this.javaPlugin, "custom-message", b.toByteArray());
        } catch (Exception e) {}
    }
}