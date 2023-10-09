package com.frostedmc.core.module.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.AccountManager;
import com.frostedmc.core.module.Module;
import com.nametag.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public class NametagModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    private NametagEdit nametagAPI;
    private AccountManager accountManager;

    public NametagModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "Nametag";
    }

    @Override
    public void onEnable() {
        this.nametagAPI = new NametagEdit();
        this.nametagAPI.onEnable(this.javaPlugin);
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        this.accountManager = Core.getInstance().getAccountManager();

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.updateTagCache(player);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.updateTag(player, "&f", "&f");
        }

        this.nametagAPI.onDisable();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        new BukkitRunnable() {
            public void run() {
                if(playerJoinEvent.getPlayer().isOnline()) {
                    updateTagCache(playerJoinEvent.getPlayer());
                }
            }
        }.runTaskLater(this.javaPlugin, 10L);
    }

    public void updateTagCache(Player player) {
        if(!this.accountManager.isRegistered(player.getUniqueId())) {
            this.accountManager.register(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress().toString());
        }
        this.accountManager.refreshCache(player.getUniqueId());
        AccountDetails accountDetails = this.accountManager.parseDetails(player.getUniqueId());
        String title = Core.getInstance().getTitleManager().parseDetails(player.getUniqueId());
        if(title == null || title.isEmpty()) {
            title = "&b";
        } else {
            title = " " + title;
        }
        this.updateTag(player, accountDetails.getRank().getPrefix(true), title);
    }

    public void revertTag(Player player) {
        if(!this.accountManager.isRegistered(player.getUniqueId())) {
            this.accountManager.register(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress().toString());
            this.accountManager.refreshCache(player.getUniqueId());
        }
        AccountDetails accountDetails = this.accountManager.parseDetails(player.getUniqueId());
        String title = Core.getInstance().getTitleManager().parseDetails(player.getUniqueId());
        if(title == null || title.isEmpty()) {
            title = "&b";
        } else {
            title = " " + title;
        }
        this.updateTag(player, accountDetails.getRank().getPrefix(true), title);
    }

    public void updateTag(Player player, String suffix) {
        if(!this.accountManager.isRegistered(player.getUniqueId())) {
            this.accountManager.register(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress().toString());
        }
        this.accountManager.refreshCache(player.getUniqueId());
        AccountDetails accountDetails = this.accountManager.parseDetails(player.getUniqueId());
        this.updateTag(player, accountDetails.getRank().getPrefix(true), suffix);
    }

    private void updateTag(Player player, String prefix, String suffix) {
        this.nametagAPI.manager.reset(player.getName());
        NametagEdit.getApi().setNametag(player, ChatColor.translateAlternateColorCodes('&', prefix), ChatColor.translateAlternateColorCodes('&', suffix));
        this.nametagAPI.handler.applyTags();
    }

    public void applyTags() {
        this.nametagAPI.handler.applyTags();
    }
}