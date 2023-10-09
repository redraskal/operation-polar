package com.frostedmc.core.module.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GlacierHook extends Module implements Listener {

    private JavaPlugin javaPlugin;
    private Map<Player, PermissionAttachment> permissionAttachmentMap = new HashMap<Player, PermissionAttachment>();

    public GlacierHook(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "GlacierHook";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        for(Player player : Bukkit.getOnlinePlayers()) {
            AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
            if(Rank.compare(accountDetails.getRank(), Rank.HELPER)) {
                PermissionAttachment permissionAttachment = player.addAttachment(this.javaPlugin);
                permissionAttachment.setPermission("glacier.notify", true);
                permissionAttachmentMap.put(player, permissionAttachment);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(event.getPlayer().getUniqueId());
        if(Rank.compare(accountDetails.getRank(), Rank.HELPER)) {
            PermissionAttachment permissionAttachment = event.getPlayer().addAttachment(this.javaPlugin);
            permissionAttachment.setPermission("glacier.notify", true);
            permissionAttachmentMap.put(event.getPlayer(), permissionAttachment);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(permissionAttachmentMap.containsKey(event.getPlayer())) {
            event.getPlayer().removeAttachment(permissionAttachmentMap.get(event.getPlayer()));
            permissionAttachmentMap.remove(event.getPlayer());
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        for(Map.Entry<Player, PermissionAttachment> entry : permissionAttachmentMap.entrySet()) {
            entry.getKey().removeAttachment(entry.getValue());
        }
        permissionAttachmentMap.clear();
    }
}