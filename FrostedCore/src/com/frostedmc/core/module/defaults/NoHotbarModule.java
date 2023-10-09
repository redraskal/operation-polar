package com.frostedmc.core.module.defaults;

import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class NoHotbarModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public NoHotbarModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "NoHotbar";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.setCustomGamemode(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.setCustomGamemode(event.getPlayer());
    }

    public void setCustomGamemode(Player player) {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1, path.length());

        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(new Class[] { Integer.TYPE, Float.TYPE });
            Object packet = playOutConstructor.newInstance(new Object[] { Integer.valueOf(3), Integer.valueOf(-1) });
            Object craftPlayerObject = craftPlayer.cast(player);
            Method getHandleMethod = craftPlayer.getMethod("getHandle", new Class[0]);
            Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
            Object pc = handle.getClass().getField("playerConnection").get(handle);
            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", new Class[] { Packet });
            sendPacketMethod.invoke(pc, new Object[] { packet });
        } catch (Exception e) {}
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
