package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.Core;
import com.frostedmc.core.module.Module;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 1/14/2017.
 */
public class MuteModule extends Module implements Listener {

    private List<UUID> muteCache = new ArrayList<UUID>();

    private Backbone instance;

    public MuteModule(Backbone instance) {
        this.instance = instance;
    }

    @Override
    public String name() {
        return "Mute";
    }

    @Override
    public void onEnable() {
        this.instance.getProxy().getPluginManager().registerListener(this.instance, this);
        this.instance.getProxy().getScheduler().schedule(this.instance, new Runnable() {
            @Override
            public void run() {
                muteCache.clear();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        this.instance.getProxy().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        UUID uuid = ((ProxiedPlayer) event.getSender()).getUniqueId();
        if(uuid != null) {
            if(muteCache.contains(uuid)) {
                ProxyServer.getInstance().getPlayer(uuid).sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lYou are currently muted and cannot chat.")));
                event.setCancelled(true);
            } else {
                if(Core.getInstance().getPunishmentManager().hasOngoingMute(uuid)) {
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lYou are currently muted and cannot chat.")));
                    event.setCancelled(true);
                    muteCache.add(uuid);
                }
            }
        }
    }
}