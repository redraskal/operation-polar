package com.frostedmc.arrowgate;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.sql.SQLDetails;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 1/28/2017.
 */
public class ArrowGate extends JavaPlugin implements Listener {

    public void onEnable() {
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{});
        Core.getInstance().getLogger().info("[FrostedGames] Initializing core engines...");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!Rank.compare(Core.getInstance().getAccountManager().parseDetails(event.getUniqueId()).getRank(),
                Rank.valueOf(this.getConfig().getString("required").toUpperCase()))) {
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("kick-message")));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
        }
    }

    public void onDisable() {
        Core.getInstance().disable();
    }
}