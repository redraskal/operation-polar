package com.nametag.plugin.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nametag.plugin.NametagManager;
import com.nametag.plugin.api.events.NametagEvent;

public class NametagAPI implements INametagApi {

    public static NametagManager manager;

    @Override
    public void setPrefix(Player player, String prefix) {
        setPrefix(player.getName(), prefix);
    }

    @Override
    public void setSuffix(Player player, String suffix) {
        setSuffix(player.getName(), suffix);
    }

    @Override
    public void setPrefix(String player, String prefix) {
        NametagEvent event = new NametagEvent(player, prefix, NametagEvent.ChangeType.PREFIX, NametagEvent.ChangeReason.API);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            manager.updateNametag(player, prefix, null);
        }
    }

    @Override
    public void setSuffix(String player, String suffix) {
        NametagEvent event = new NametagEvent(player, suffix, NametagEvent.ChangeType.SUFFIX, NametagEvent.ChangeReason.API);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            manager.updateNametag(player, null, suffix);
        }
    }

    @Override
    public void setNametag(Player player, String prefix, String suffix) {
        manager.overlapNametag(player.getName(), prefix, suffix);
    }

    @Override
    public void setNametag(String player, String prefix, String suffix) {
        manager.overlapNametag(player, prefix, suffix);
    }

}