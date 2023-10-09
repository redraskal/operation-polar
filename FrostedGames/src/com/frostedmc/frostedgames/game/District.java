package com.frostedmc.frostedgames.game;

import org.bukkit.ChatColor;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public enum District {

    DISTRICT_1(1, ChatColor.DARK_RED),
    DISTRICT_2(2, ChatColor.RED),
    DISTRICT_3(3, ChatColor.GOLD),
    DISTRICT_4(4, ChatColor.YELLOW),
    DISTRICT_5(5, ChatColor.DARK_GREEN),
    DISTRICT_6(6, ChatColor.GREEN),
    DISTRICT_7(7, ChatColor.AQUA),
    DISTRICT_8(8, ChatColor.DARK_AQUA),
    DISTRICT_9(9, ChatColor.BLUE),
    DISTRICT_10(10, ChatColor.LIGHT_PURPLE),
    DISTRICT_11(11, ChatColor.DARK_PURPLE),
    DISTRICT_12(12, ChatColor.WHITE);

    private int number;
    private ChatColor chatColor;

    private District(int number, ChatColor chatColor) {
        this.number = number;
        this.chatColor = chatColor;
    }

    public String getTag() {
        return this.chatColor + "[D" + this.number + "]";
    }

    public int getNumber() {
        return this.number;
    }
}