package com.frostedmc.core.api.redis;

import com.frostedmc.core.Core;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;

/**
 * Created by Redraskal_2 on 8/29/2016.
 */
public class RedisServerManager implements PluginMessageListener {

    private static RedisServerManager instance;

    public static RedisServerManager getInstance() {
        return instance;
    }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }

        instance = new RedisServerManager(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;
    private List<RedisCallback> onlinePlayerCallbacks;
    private Map<RedisCallback, String> serverLookupCallbacks;

    private RedisServerManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        long start = System.currentTimeMillis();
        Core.getInstance().getLogger().info("[Manager] Enabling Redis Server Manager...");

        this.onlinePlayerCallbacks = new ArrayList<RedisCallback>();
        this.serverLookupCallbacks = new HashMap<RedisCallback, String>();

        this.registerChannel("FrostedChannel");
        this.registerChannel("RedisBungee");
        this.registerChannel("BungeeCord");

        long end = System.currentTimeMillis();
        long time = (end - start);
        Core.getInstance().getLogger().info("[Manager] Enabled Redis Server Manager in " + time + " ms.");
    }

    private void registerChannel(String channel) {
        Core.getInstance().getLogger().info("[Manager] Registering " + channel + " channel...");

        Bukkit.getMessenger().registerIncomingPluginChannel(this.javaPlugin, channel, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.javaPlugin, channel);
    }

    public void queryOnlinePlayers(RedisCallback redisCallback) {
        this.onlinePlayerCallbacks.add(redisCallback);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF("ALL");
        getRandomPlayer().sendPluginMessage(this.javaPlugin, "RedisBungee", out.toByteArray());
    }

    public void sendPlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this.javaPlugin, "BungeeCord", out.toByteArray());
    }

    public void queryServerPrefix(String prefix, RedisCallback redisCallback) {
        this.serverLookupCallbacks.put(redisCallback, prefix);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ServerPrefix");
        out.writeUTF(prefix);
        getRandomPlayer().sendPluginMessage(this.javaPlugin, "FrostedChannel", out.toByteArray());
    }

    private Player getRandomPlayer() {
        Player[] array = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
        return array[new Random().nextInt(array.length)];
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if(s.equalsIgnoreCase("RedisBungee") || s.equalsIgnoreCase("FrostedChannel") || s.equalsIgnoreCase("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

            try {
                String action = in.readUTF();

                switch(action) {
                    case "PlayerCount": {
                        String type = in.readUTF();
                        int count = in.readInt();

                        for(RedisCallback callback : this.onlinePlayerCallbacks) {
                            callback.callback(count);
                        }

                        this.onlinePlayerCallbacks.clear();
                    } case "ServerPrefix": {
                        String prefix = in.readUTF();
                        String[] result = in.readUTF().split(",");
                        List<RedisCallback> removeQuery = new ArrayList<RedisCallback>();

                        for(Map.Entry<RedisCallback, String> callback : this.serverLookupCallbacks.entrySet()) {
                            if (callback.getValue().equalsIgnoreCase(prefix)) {
                                callback.getKey().callback(result);
                                removeQuery.add(callback.getKey());
                            }
                        }

                        for(RedisCallback callback : removeQuery) {
                            this.serverLookupCallbacks.remove(callback);
                        }
                    }
                }
            } catch (Exception e) {}
        }
    }
}