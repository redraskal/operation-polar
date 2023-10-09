package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.module.Module;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

/**
 * Created by Redraskal_2 on 1/7/2017.
 */
public class TwoFactorAuthModule extends Module implements Listener {

    private Backbone instance;

    public TwoFactorAuthModule(Backbone instance) {
        this.instance = instance;
    }

    @Override
    public String name() {
        return "TwoFactorAuth";
    }

    @Override
    public void onEnable() {
        this.instance.getProxy().getPluginManager().registerListener(this.instance, this);
        BungeeCord.getInstance().registerChannel("chrome-push");
        RedisBungee.getApi().registerPubSubChannels("chrome-approve");
        RedisBungee.getApi().registerPubSubChannels("chrome-deny");
    }

    @Override
    public void onDisable() {
        this.instance.getProxy().getPluginManager().unregisterListener(this);
        BungeeCord.getInstance().unregisterChannel("chrome-push");
        RedisBungee.getApi().unregisterPubSubChannels("chrome-approve");
        RedisBungee.getApi().unregisterPubSubChannels("chrome-deny");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getTag().equalsIgnoreCase("chrome-push")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String utf = in.readUTF();
                RedisBungee.getApi().sendChannelMessage("chrome-push", utf);
            } catch (IOException e) {}
        }
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if(event.getChannel().equalsIgnoreCase("chrome-approve")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF(event.getMessage().replaceAll("^\"|\"$", ""));
                for(ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    serverInfo.sendData("chrome-approve", b.toByteArray());
                }
            } catch (IOException e) {}
        }
        if(event.getChannel().equalsIgnoreCase("chrome-deny")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF(event.getMessage().replaceAll("^\"|\"$", ""));
                for(ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    serverInfo.sendData("chrome-deny", b.toByteArray());
                }
            } catch (IOException e) {}
        }
    }
}