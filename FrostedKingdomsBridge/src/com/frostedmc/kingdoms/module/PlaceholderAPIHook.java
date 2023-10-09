package com.frostedmc.kingdoms.module;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class PlaceholderAPIHook extends Module {

    private JavaPlugin javaPlugin;

    public PlaceholderAPIHook(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "PlaceholderAPIHook";
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholder(this.javaPlugin, "frostedrank", new PlaceholderReplacer(){
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Rank rank = Core.getInstance().getAccountManager().parseDetails(e.getPlayer().getUniqueId()).getRank();
                    if(rank == Rank.PLAYER) {
                        return ChatColor.translateAlternateColorCodes('&', "&7&lPLAYER");
                    } else {
                        return rank.getPrefix(false);
                    }
                }
            });
            PlaceholderAPI.registerPlaceholder(this.javaPlugin, "frostedicicles", new PlaceholderReplacer(){
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    return "" + Core.getInstance().getAccountManager().parseDetails(e.getPlayer().getUniqueId()).getIcicles();
                }
            });
            PlaceholderAPI.registerPlaceholder(this.javaPlugin, "frostkingdomsrp", new PlaceholderReplacer(){
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
                    String rp = kp.getKingdom() != null ? String.valueOf(kp.getKingdom().getResourcepoints()) : "0";
                    return rp;
                }
            });
            PlaceholderAPI.registerPlaceholder(this.javaPlugin, "frostkingdomsland", new PlaceholderReplacer() {
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
                    String land = kp.getKingdom() != null ? String.valueOf(kp.getKingdom().getLand()) : "0";
                    return land;
                }
            });
        }
    }

    @Override
    public void onDisable() {}
}