package com.nametag.plugin;

import com.nametag.plugin.api.events.NametagEvent;
import com.nametag.plugin.storage.data.PlayerData;
import com.nametag.plugin.utils.UUIDFetcher;
import com.nametag.plugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NametagHandler implements Listener {

    private boolean tabListDisabled;
    @SuppressWarnings("unused")
	private boolean fancyMessageCompatible = true;

    private Map<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();

    private NametagEdit plugin;
    private JavaPlugin javaPlugin;

    public NametagHandler(NametagEdit plugin, JavaPlugin javaPlugin) {
        this.plugin = plugin;
        this.javaPlugin = javaPlugin;
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);
        this.tabListDisabled = false;
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        this.fancyMessageCompatible = version.startsWith("v1_8");
    }

    public PlayerData getPlayerData(Player player) {
        if (player == null) return null;
        return playerData.get(player.getUniqueId());
    }

    public void reload() {
        //Nope
    }

    public void clear(final CommandSender sender, final String player) {
        Player target = Bukkit.getPlayerExact(player);
        if (target != null) {
            handleClear(target.getUniqueId(), player);
            return;
        }

        UUIDFetcher.lookupUUID(player, javaPlugin, new UUIDFetcher.UUIDLookup() {
            @Override
            public void response(UUID uuid) {
                if (uuid == null) {
                    //NametagMessages.UUID_LOOKUP_FAILED.send(sender);
                } else {
                    handleClear(uuid, player);
                }
            }
        });
    }

    public void save(final CommandSender sender, String targetName, NametagEvent.ChangeType changeType, String value) {
        Player player = Bukkit.getPlayerExact(targetName);

        PlayerData data = getPlayerData(player);
        if (data == null) {
            data = new PlayerData(targetName, null, "", "");
            if (player != null) {
                playerData.put(player.getUniqueId(), data);
            }
        }

        if (changeType == NametagEvent.ChangeType.PREFIX) {
            data.setPrefix(value);
            plugin.getManager().overlapNametag(targetName, Utils.format(value, true), Utils.format(data.getSuffix(), true));
        } else {
            data.setSuffix(value);
            plugin.getManager().overlapNametag(targetName, Utils.format(data.getPrefix(), true), Utils.format(value, true));
        }

        final PlayerData finalData = data;
        UUIDFetcher.lookupUUID(targetName, javaPlugin, new UUIDFetcher.UUIDLookup() {
            @Override
            public void response(UUID uuid) {
                if (uuid == null) {
                    //NametagMessages.UUID_LOOKUP_FAILED.send(sender);
                } else {
                    playerData.put(uuid, finalData);
                    finalData.setUUID(uuid);
                }
            }
        });
    }

    /**
     * Cleans up any nametag data on the server to prevent memory leaks
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getManager().reset(event.getPlayer().getName());
        plugin.getManager().clearFromCache(event.getPlayer());
    }

    /**
     * Applies tags to a player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.getManager().sendTeams(player);
        plugin.getManager().reset(player.getName());
    }

    private void handleClear(UUID uuid, String player) {
        playerData.remove(uuid);
        plugin.getManager().reset(player);
    }

    public void applyTags() {
        for (Player online : Utils.getOnline()) {
            if (online != null) {
                applyTagToPlayer(online);
            }
        }
    }

    public void applyTagToPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerData.get(uuid);
        if (data != null) {
            plugin.getManager().updateNametag(player.getName(), Utils.format(data.getPrefix(), true), Utils.format(data.getSuffix(), true));
        } else {
           //TODO Warning
        }

        if (tabListDisabled) {
            player.setPlayerListName(Utils.format("&f" + player.getName(), true));
        } else {
            player.setPlayerListName(null);
        }
    }

}