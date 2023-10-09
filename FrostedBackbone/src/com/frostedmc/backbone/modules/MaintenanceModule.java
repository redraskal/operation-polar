package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.module.Module;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class MaintenanceModule extends Module implements Listener {

    private Plugin plugin;

    public MaintenanceModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "Maintenance";
    }

    @Override
    public void onEnable() {
        if(!Backbone.getInstance().getMaintenanceManager().exists()) {
            Backbone.getInstance().getMaintenanceManager().register(false);
        }

        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);

        for(ProxiedPlayer proxiedPlayer : this.plugin.getProxy().getPlayers()) {
            if(!this.validate(proxiedPlayer.getUniqueId())) {
                proxiedPlayer.disconnect(new TextComponent(this.getMessage()));
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PreLoginEvent preLoginEvent) throws SQLException {
        if(!this.validate(this.getUUID(preLoginEvent.getConnection().getName()))) {
            preLoginEvent.setCancelReason(this.getMessage());
            preLoginEvent.setCancelled(true);
        }
    }

    public UUID getUUID(String username) {
        try {
            ResultSet result = Core.getInstance().getSQLConnection().getConnection().createStatement()
                    .executeQuery("SELECT uuid FROM `account` WHERE username='" + username + "';");
            if(!result.next()) {
                return null;
            }
            return UUID.fromString(result.getString("uuid"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onDisable() {
        this.plugin.getProxy().getPluginManager().unregisterListener(this);
    }

    private boolean validate(UUID uuid) {
        if(Backbone.getInstance().getMaintenanceManager().parseDetails(false)) {
            if(uuid == null) { return false; }
            if(Core.getInstance().getAccountManager().isRegistered(uuid)) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(uuid).getRank();

                if(Rank.compare(rank, Rank.BUILDER)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    private String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', "&c&lFrostedMC is currently in Maintenance Mode.\n&7Check out &bfrostedmc.com &7for more details!");
    }
}
