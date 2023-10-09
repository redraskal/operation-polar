package com.frostedmc.kingdoms.module;

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

public class PermissionHook extends Module implements Listener {

    private JavaPlugin javaPlugin;
    private Map<Player, PermissionAttachment> permissionAttachmentMap = new HashMap<Player, PermissionAttachment>();

    public PermissionHook(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "PermissionHook";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        for(Player player : Bukkit.getOnlinePlayers()) {
            this.set(player);
        }
    }

    private void set(Player player) {
        AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
        PermissionAttachment permissionAttachment = player.addAttachment(this.javaPlugin);
        permissionAttachment.setPermission("kingdoms.player", true);
        permissionAttachment.setPermission("recount.data", true);
        permissionAttachment.setPermission("recount.show", true);
        permissionAttachment.setPermission("recount.toggle", true);
        permissionAttachment.setPermission("recount.helper", true);
        permissionAttachment.setPermission("supplycrates.time", true);
        permissionAttachment.setPermission("supplycrates.drops", true);
        permissionAttachment.setPermission("essentials.spawn", true);
        permissionAttachment.setPermission("essentials.kits.starter", true);
        permissionAttachment.setPermission("essentials.kits.food", true);
        permissionAttachment.setPermission("essentials.signs.use.sell", true);
        permissionAttachment.setPermission("essentials.signs.use.buy", true);
        permissionAttachment.setPermission("essentials.clearinventory", true);
        permissionAttachment.setPermission("essentials.home", true);
        permissionAttachment.setPermission("essentials.sethome", true);
        permissionAttachment.setPermission("plugins.see", true);
        permissionAttachment.setPermission("chestcommands.open.warpgui.yml", true);
        permissionAttachment.setPermission("silkspawners.explodedrop", true);
        permissionAttachment.setPermission("silkspawners.place.*", true);
        permissionAttachment.setPermission("ecodrop.pickup", true);
        permissionAttachment.setPermission("auctionhouse.use", true);
        permissionAttachment.setPermission("auctionhouse.sell", true);
        permissionAttachment.setPermission("auctionhouse.limit.20", true);
        permissionAttachment.setPermission("growingores.mine", true);
        permissionAttachment.setPermission("banknotes.withdraw", true);
        permissionAttachment.setPermission("banknotes.deposit", true);
        permissionAttachment.setPermission("proquests.menu.open.*", true);
        permissionAttachment.setPermission("proquests.start.*", true);
        permissionAttachment.setPermission("potionshop.use", true);
        permissionAttachment.setPermission("duels.duel", true);
        permissionAttachment.setPermission("zenchantments.enchant.get", true);
        permissionAttachment.setPermission("zenchantments.enchant.use", true);
        permissionAttachment.setPermission("zenchantments.command.list", true);
        permissionAttachment.setPermission("flyzone.freeareas", true);
        permissionAttachment.setPermission("shopguiplus.shops.blocks", true);
        permissionAttachment.setPermission("shopguiplus.shops.food", true);
        if(Rank.compare(accountDetails.getRank(), Rank.VIP)) {
            permissionAttachment.setPermission("essentials.kits.vip", true);
            permissionAttachment.setPermission("essentials.workbench", true);
            permissionAttachment.setPermission("essentials.hat", true);
            permissionAttachment.setPermission("essentials.sethome.multiple.vip", true);
            permissionAttachment.setPermission("silkspawners.silkdrop.*", true);
            permissionAttachment.setPermission("essentials.repair", true);
            permissionAttachment.setPermission("essentials.repair.enchanted", true);
            permissionAttachment.setPermission("flyzone.donatorareas", true);
        }
        if(Rank.compare(accountDetails.getRank(), Rank.ELITE)) {
            permissionAttachment.setPermission("essentials.kits.elite", true);
            permissionAttachment.setPermission("essentials.recipe", true);
            permissionAttachment.setPermission("essentials.enderchest", true);
            permissionAttachment.setPermission("essentials.sethome.multiple.elite", true);
            permissionAttachment.setPermission("essentials.sell", true);
            permissionAttachment.setPermission("essentials.sell.hand", true);
        }
        if(Rank.compare(accountDetails.getRank(), Rank.LEGEND)) {
            permissionAttachment.setPermission("essentials.kits.legend", true);
            permissionAttachment.setPermission("essentials.near", true);
            permissionAttachment.setPermission("essentials.feed", true);
            permissionAttachment.setPermission("essentials.sethome.multiple.legend", true);
            permissionAttachment.setPermission("essentials.repair.all", true);
            permissionAttachment.setPermission("essentials.repair.armor", true);
            permissionAttachment.setPermission("essentials.fly", true);
            permissionAttachment.setPermission("playervaults.amount.1", true);
        }
        if(Rank.compare(accountDetails.getRank(), Rank.HELPER)) {
            permissionAttachment.setPermission("essentials.vanish", true);
            permissionAttachment.setPermission("essentials.tpo", true);
        }
        if(Rank.compare(accountDetails.getRank(), Rank.ADMIN)) {
            permissionAttachment.setPermission("essentials.invsee", true);
        }
        permissionAttachmentMap.put(player, permissionAttachment);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.set(event.getPlayer());
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