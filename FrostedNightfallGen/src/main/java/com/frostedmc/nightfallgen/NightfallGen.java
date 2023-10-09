package com.frostedmc.nightfallgen;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class NightfallGen extends JavaPlugin implements Listener {

    @Getter private File worldFolder;
    @Getter private File settingsFolder;
    @Getter private Socket socket;

    public void onEnable() {
        this.worldFolder = new File("/home/nightfall/worlds/");
        this.settingsFolder = new File("/home/nightfall/settings/");
        this.modifyBukkitWorldContainer("/home/nightfall/worlds/");

        this.getServer().getPluginManager().registerEvents(this, this);
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;

            this.socket = IO.socket("http://localhost:3794", options);
            socket.on(Socket.EVENT_CONNECT, args -> {
                this.getLogger().info("[WorldManager] Established connection to socket server.");
                socket.emit("server_info", Bukkit.getServerName());
            }).on("world_create", args -> {
                UUID uuid = UUID.fromString((String) args[0]);

                File worldFolder = new File(this.getWorldFolder(), uuid.toString() + "/");
                if(!worldFolder.exists()) {
                    new BukkitRunnable() {
                        public void run() {
                            try {
                                FileUtils.quickCopy(getSettingsFolder(), new File(worldFolder, "settings/"));

                                World world = Bukkit.getServer().createWorld(new WorldCreator(uuid.toString()));

                                world.setGameRuleValue("doFireTick", "false");
                                world.setGameRuleValue("doMobSpawning", "false");
                                world.setGameRuleValue("randomTickSpeed", "0");
                                world.setGameRuleValue("doDaylightCycle", "false");

                                WorldServer handle = ((CraftWorld) world).getHandle();

                                short short1 = 196;
                                long i = System.currentTimeMillis();
                                for (int j = -short1; j <= short1; j += 16) {
                                    for (int k = -short1; k <= short1; k += 16) {
                                        long l = System.currentTimeMillis();

                                        if (l < i) {
                                            i = l;
                                        }

                                        if (l > i + 1000L) {
                                            int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                                            int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

                                            System.out.println("Preparing spawn area for " + uuid.toString() + ", " + (j1 * 100 / i1) + "%");
                                            socket.emit("world_status", uuid.toString(), "Preparing spawn area, " + (j1 * 100 / i1) + "% complete");
                                            i = l;
                                        }

                                        BlockPosition chunkcoordinates = handle.getSpawn();
                                        handle.chunkProviderServer.getChunkAt(chunkcoordinates.getX() + j >> 4, chunkcoordinates.getZ() + k >> 4);
                                    }
                                }

                                socket.emit("world_status", uuid.toString(), "Uploading chunks to cloud (this may take a while)...");

                                String worldName = world.getName();
                                long seed = world.getSeed();

                                world.setAutoSave(false);
                                world.getEntities().forEach(entity -> entity.remove());
                                for(Chunk chunk : world.getLoadedChunks()) chunk.unload(true);
                                Bukkit.getServer().unloadWorld(worldName, true);

                                File sessionLock = new File(worldFolder, "session.lock");
                                if(sessionLock.exists()) {
                                    sessionLock.setReadable(true);
                                    sessionLock.setWritable(true);
                                    sessionLock.setExecutable(true);
                                    sessionLock.delete();
                                }

                                File seedFile = new File(worldFolder, "seed.dat");
                                seedFile.createNewFile();

                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(seedFile));
                                bufferedWriter.write("" + seed);
                                bufferedWriter.close();

                                //socket.emit("world_status", uuid.toString(), "Waiting for file lock to be released...");
                                socket.emit("world_status", uuid.toString(), "success");
                            } catch (IOException e) {
                                socket.emit("world_status", uuid.toString(), "error Error while copying world settings.");
                            }
                        }
                    }.runTaskLater(this, 1L);
                } else {
                    socket.emit("world_status", uuid.toString(), "error Fortress world already exists.");
                }
            }).on(Socket.EVENT_DISCONNECT, args -> this.getLogger().severe("[WorldManager] Socket connection has been interrupted, attempting to reconnect."));
            socket.connect();
        } catch (URISyntaxException e) {
            this.getLogger().severe("[WorldManager] Socket connection could not be initialized.");
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

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are not allowed to join this server.");
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }
}