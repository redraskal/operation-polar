package com.frostedmc.core.messages;

import org.bukkit.ChatColor;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public enum PrefixType {

    ADVERT(ChatColor.DARK_RED),
    NETWORK(ChatColor.GOLD),
    OTHER(ChatColor.AQUA);

    private ChatColor chatColor;

    private PrefixType(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }
}