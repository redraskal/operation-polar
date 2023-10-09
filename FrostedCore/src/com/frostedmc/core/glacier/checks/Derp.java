package com.frostedmc.core.glacier.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.frostedmc.core.glacier.Check;
import com.frostedmc.core.glacier.GlacierModule;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 11/22/2016.
 */
public class Derp extends Check {

    private PacketListener packetListener;

    public Derp() {
        super("Derp", true);
        this.bannable = true;
        this.countToNotify = 1;
        this.maxViolations = 50;
    }

    public void onEnable() {
        this.packetListener = new PacketAdapter(GlacierModule.getInstance().getPlugin(), new PacketType[]{PacketType.Play.Client.LOOK}) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if(player == null)
                    return;
                float pitch = event.getPacket().getFloat().read(0).floatValue();
                float yaw = event.getPacket().getFloat().read(1).floatValue();
                log(player.getUniqueId(), "[p: " + pitch + ", y: "
                        + yaw + "]");
                if(pitch > 90.1 || pitch < -90.1) {
                    log(player.getUniqueId(), "[p: " + pitch + ", y: "
                            + yaw + "]");
                    GlacierModule.getInstance().handleViolation(player, GlacierModule.getInstance().getCheck("Derp"));
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener);
    }

    public void onDisable() {
        if(this.packetListener == null)
            return;
        ProtocolLibrary.getProtocolManager().removePacketListener(this.packetListener);
    }
}