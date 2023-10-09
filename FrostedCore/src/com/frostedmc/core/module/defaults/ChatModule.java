package com.frostedmc.core.module.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.events.PlayerChatEvent;
import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/12/2016.
 */
public class ChatModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public ChatModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "Chat";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if(StatisticsCache.getInstance() != null) {
            if(StatisticsCache.getInstance().get(asyncPlayerChatEvent.getPlayer(), "options_chat") == 1) {
                asyncPlayerChatEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bChat> &7You currently have &eChat Messages &7disabled in /options."));
                asyncPlayerChatEvent.setCancelled(true);
                return;
            }
        }
        Player player = asyncPlayerChatEvent.getPlayer();
        AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
        String message = asyncPlayerChatEvent.getMessage();
        message = ChatColor.stripColor(message);
        PlayerChatEvent playerChatEvent = new PlayerChatEvent(player, message);
        this.javaPlugin.getServer().getPluginManager().callEvent(playerChatEvent);
        message = ChatColor.translateAlternateColorCodes('&', playerChatEvent.getMessage());
        message = ChatColor.stripColor(message);

        if(!playerChatEvent.isCancelled() && !asyncPlayerChatEvent.isCancelled()) {
            String finalizedMessage = ChatColor.translateAlternateColorCodes('&',
                    accountDetails.getRank().getPrefix(true)
                            + "&7"
                            + player.getName()
                            + " &f" + message
            );
            for(Player other : Bukkit.getOnlinePlayers()) {
                if(StatisticsCache.getInstance() != null) {
                    if(StatisticsCache.getInstance().get(other, "options_chat") == 0) {
                        if(!asyncPlayerChatEvent.getPlayer().getName().equalsIgnoreCase(other.getName())) {
                            testForMentions(finalizedMessage, other);
                        } else {
                            other.sendMessage(finalizedMessage);
                        }
                    }
                } else {
                    other.sendMessage(finalizedMessage);
                }
            }
        }

        asyncPlayerChatEvent.setCancelled(true);
    }

    private void testForMentions(String message, Player toSend) {
        if(message.contains(toSend.getName())) {
            if(StatisticsCache.getInstance().get(toSend, "options_mentions") == 1) {
                toSend.playSound(toSend.getLocation(), Sound.ORB_PICKUP, 5, 1);
                String temp = "";
                for(String word : message.split(" ")) {
                    if(!temp.isEmpty()) temp+=" ";
                    if(word.contains(toSend.getName())) {
                        temp+=ChatColor.YELLOW + word + ChatColor.WHITE;
                    } else {
                        temp+=word;
                    }
                }
                toSend.sendMessage(temp);
            } else {
                toSend.sendMessage(message);
            }
        } else {
            toSend.sendMessage(message);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}