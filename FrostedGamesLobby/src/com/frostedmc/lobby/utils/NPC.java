package com.frostedmc.lobby.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.techcable.npclib.nms.versions.v1_8_R3.NMS;
import net.techcable.npclib.nms.versions.v1_8_R3.network.NPCConnection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class NPC implements Listener {

    private CustomPlayer customPlayer;
    private NPCInteractEvent npcInteractEvent;

    public NPC(JavaPlugin javaPlugin, String displayName, UUID skinUUID, Location location, NPCInteractEvent npcInteractEvent) {
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
        this.customPlayer = new CustomPlayer(location, displayName, skinUUID, npcInteractEvent);
        this.npcInteractEvent = npcInteractEvent;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.customPlayer);
        NMS.sendToAll(packet);
        this.customPlayer.spawnIn(NMS.getHandle(location.getWorld()));
        this.customPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        NMS.getHandle(location.getWorld()).addEntity(this.customPlayer);
        look(location.getPitch(), location.getYaw());
    }

    public void look(float pitch, float yaw) {
        yaw = clampYaw(yaw);
        this.customPlayer.yaw = yaw;
        this.customPlayer.pitch = pitch;
        this.customPlayer.aK = yaw; // MCP -- rotationYawHead Srg -- field_70759_as
        if (this.customPlayer instanceof EntityHuman)
            this.customPlayer.aI = yaw; // MCP -- renderYawOffset Srg -- field_70761_aq
        this.customPlayer.aL = yaw; // MCP -- prevRotationYawHead Srg -- field_70758_at
    }

    public float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.customPlayer);
        NMS.send(packet, playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent playerInteractEntityEvent) {
        if(playerInteractEntityEvent.getRightClicked().getUniqueId() == this.customPlayer.getUniqueID()) {
            this.npcInteractEvent.on(playerInteractEntityEvent.getPlayer());
        }
    }

    public CustomPlayer getPlayer() {
        return this.customPlayer;
    }

    public void despawn() {
        HandlerList.unregisterAll(this);
        this.customPlayer.getBukkitEntity().remove();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.customPlayer);
        NMS.sendToAll(packet);
    }

    public class CustomPlayer extends EntityPlayer {

        private NPCInteractEvent npcInteractEvent;

        public CustomPlayer(Location location, String username, UUID uuid, NPCInteractEvent npcInteractEvent) {
            super(NMS.getServer(), NMS.getHandle(location.getWorld()), makeProfile(username, uuid), new PlayerInteractManager(NMS.getHandle(location.getWorld())));
            playerInteractManager.b(WorldSettings.EnumGamemode.CREATIVE); //MCP = initializeGameType ---- SRG=func_73077_b
            playerConnection = new NPCConnection(this);
            this.npcInteractEvent = npcInteractEvent;
            setPosition(location.getX(), location.getY(), location.getZ());
        }

        public boolean a(EntityHuman entity) {
            npcInteractEvent.on((Player) entity.getBukkitEntity());
            return false;
        }

        public void h() {
            super.h();
            this.C();

            //Apply velocity etc.
            this.motY = onGround ? Math.max(0.0, motY) : motY;
            move(motX, motY, motZ);
            this.motX *= 0.800000011920929;
            this.motY *= 0.800000011920929;
            this.motZ *= 0.800000011920929;
            if (!this.onGround) {
                this.motY -= 0.1; //Most random value, don't judge.
            }
        }

        @Override
        public boolean damageEntity(DamageSource source, float damage) {
            return super.damageEntity(source, damage);
        }
    }

    private static GameProfile makeProfile(String username, UUID uuid) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), username);
        NMS.setSkin(profile, uuid);
        return profile;
    }
}