package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.module.Module;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class MOTDModule extends Module implements Listener {

    private Plugin plugin;

    public MOTDModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "MOTD";
    }

    @Override
    public void onEnable() {
        this.register();
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent proxyPingEvent) {
        ServerPing response = proxyPingEvent.getResponse();
        response.setDescriptionComponent(
                new TextComponent(ChatColor.translateAlternateColorCodes('&', Backbone.getInstance().getMOTDManager().parseDetails())));
        if(Backbone.getInstance().getMaintenanceManager().exists()) {
            if(Backbone.getInstance().getMaintenanceManager().parseDetails(false)) {
                response.setVersion(new ServerPing.Protocol("Maintenance Mode", (response.getVersion().getProtocol()-1)));
            }
        }
        response.setPlayers(new ServerPing.Players((ProxyServer.getInstance().getServers().size()*80),
                RedisBungee.getApi().getPlayerCount(), new ServerPing.PlayerInfo[]{}));
        proxyPingEvent.setResponse(response);
    }

    @Override
    public void onDisable() {
        this.plugin.getProxy().getPluginManager().unregisterListener(this);
    }

    private void register() {
        if(!Backbone.getInstance().getMOTDManager().exists()) {
            Backbone.getInstance().getMOTDManager().register("&b&lFROSTED &6[Coming Soon] &8| &3Patch 0.0.1\n&6&lUPDATES » &c&lNetwork in Maintenance Mode.");
        }
    }
}