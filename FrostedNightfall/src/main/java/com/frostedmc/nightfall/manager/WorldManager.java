package com.frostedmc.nightfall.manager;

import com.frostedmc.nightfall.Nightfall;
import com.frostedmc.nightfall.callback.FortressWorldLoadCallback;
import com.frostedmc.nightfall.listener.WorldManagerListener;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class WorldManager {

    @Getter private final Nightfall nightfall;
    @Getter private final File worldFolder;
    @Getter private Socket socket;

    private Map<UUID, FortressWorldLoadCallback> callbackMap = new HashMap<>();

    public WorldManager(Nightfall nightfall) {
        this.nightfall = nightfall;
        this.worldFolder = new File("/home/nightfall/worlds/");
        this.modifyBukkitWorldContainer("/home/nightfall/worlds/");
        this.getNightfall().getServer().getPluginManager()
                .registerEvents(new WorldManagerListener(this), this.getNightfall());
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;

            this.socket = IO.socket("http://localhost:3794", options);
            socket.on(Socket.EVENT_CONNECT, args -> {
                this.getNightfall().getLogger().info("[WorldManager] Established connection to socket server.");
                socket.emit("server_info", Bukkit.getServerName());
            }).on("world_status", args -> {
                UUID uuid = UUID.fromString((String) args[0]);
                String message = "";

                for(int i=1; i<args.length; i++) {
                    if(!message.isEmpty()) message+=" ";
                    message+=args[i];
                }

                if(callbackMap.containsKey(uuid)) {
                    if(message.equalsIgnoreCase("success")) {
                        new BukkitRunnable() {
                            public void run() {
                                File worldFolder = new File(getWorldFolder(), uuid.toString() + "/");
                                File seedFile = new File(worldFolder, "seed.dat");
                                if(worldFolder.exists() && worldFolder.listFiles().length > 0 && seedFile.exists()) {
                                    WorldCreator worldCreator = new WorldCreator(uuid.toString());

                                    try {
                                        BufferedReader bufferedReader = new BufferedReader(new FileReader(seedFile));
                                        long seed = Long.parseLong(bufferedReader.readLine());
                                        bufferedReader.close();
                                        worldCreator.seed(seed);

                                        World world = Bukkit.getServer().createWorld(worldCreator);

                                        world.setGameRuleValue("doFireTick", "false");
                                        world.setGameRuleValue("doMobSpawning", "false");
                                        world.setGameRuleValue("randomTickSpeed", "0");
                                        world.setGameRuleValue("doDaylightCycle", "true");

                                        callbackMap.get(uuid).done(world);
                                    } catch (Exception e) {
                                        callbackMap.get(uuid).error("Something went wrong while generating the world.");
                                    }
                                } else {
                                    callbackMap.get(uuid).error("Something went wrong while generating the world.");
                                }
                                callbackMap.remove(uuid);
                            }
                        }.runTaskLater(this.getNightfall(), 1L);
                    } else {
                        if(message.startsWith("error ")) {
                            callbackMap.get(uuid).error(message.replaceFirst("error ", ""));
                            callbackMap.remove(uuid);
                        } else {
                            callbackMap.get(uuid).progress(message);
                        }
                    }
                } else {
                    this.getNightfall().getLogger().warning("[WorldManager] Information received for " + uuid.toString() + ", but user is not online.");
                }
            }).on(Socket.EVENT_DISCONNECT, args -> this.getNightfall().getLogger().severe("[WorldManager] Socket connection has been interrupted, attempting to reconnect."));
            socket.connect();
        } catch (URISyntaxException e) {
            this.getNightfall().getLogger().severe("[WorldManager] Socket connection could not be initialized.");
        }
    }

    private void modifyBukkitWorldContainer(String worldContainer) {
        String _package = Bukkit.getServer().getClass().getPackage().getName();
        _package = _package.substring(_package.lastIndexOf(".") + 1);

        try {
            Class<?> c_bukkit = Class.forName("org.bukkit.Bukkit");
            Class<?> c_craftServer = Class.forName("org.bukkit.craftbukkit." + _package + ".CraftServer");

            Field f_container = c_craftServer.getDeclaredField("container");
            Method m_getServer = c_bukkit.getDeclaredMethod("getServer");

            Object craftServer = c_craftServer.cast(m_getServer.invoke(null));
            f_container.setAccessible(true);
            f_container.set(craftServer, new File(worldContainer));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean isServiceAvailable() {
        return (this.getSocket() != null && this.getSocket().connected());
    }

    public boolean doesPlayerHaveWorld(UUID uuid) {
        File worldFolder = new File(this.getWorldFolder(), uuid.toString() + "/");
        return worldFolder.exists();
    }

    public void loadFortressWorld(UUID uuid, FortressWorldLoadCallback callback) {
        if(!this.isServiceAvailable()) {
            callback.error("World Manager service is currently unavailable.");
        } else {
            if(this.callbackMap.containsKey(uuid)) {
                callback.error("World is already loading, please wait.");
            } else {
                File worldFolder = new File(this.getWorldFolder(), uuid.toString() + "/");
                if(worldFolder.exists() && worldFolder.listFiles().length > 0) {
                    File sessionLock = new File(worldFolder, "session.lock");
                    if(sessionLock.exists()) {
                        callback.error("World is already loaded on another session.");
                    } else {
                        World world = Bukkit.getServer().createWorld(new WorldCreator(uuid.toString()));

                        world.setGameRuleValue("doFireTick", "false");
                        world.setGameRuleValue("doMobSpawning", "false");
                        world.setGameRuleValue("randomTickSpeed", "0");
                        world.setGameRuleValue("doDaylightCycle", "true");

                        callback.done(world);
                    }
                } else {
                    callbackMap.put(uuid, callback);
                    socket.emit("world_create", uuid.toString());
                }
            }
        }
    }

    public boolean unloadFortressWorld(UUID uuid) {
        World world = Bukkit.getWorld(uuid.toString());

        if(world == null) return false;
        world.getPlayers().forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

        File worldFolder = world.getWorldFolder();
        String worldName = world.getName();

        //world.setAutoSave(false);
        world.getEntities().forEach(entity -> entity.remove());
        //for(Chunk chunk : world.getLoadedChunks()) chunk.unload(true);
        Bukkit.getServer().unloadWorld(worldName, true);

        File sessionLock = new File(worldFolder, "session.lock");
        if(sessionLock.exists()) {
            sessionLock.setReadable(true);
            sessionLock.setWritable(true);
            sessionLock.setExecutable(true);
            sessionLock.delete();
        }

        return true;
    }
}
