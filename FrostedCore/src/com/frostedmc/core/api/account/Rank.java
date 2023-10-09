package com.frostedmc.core.api.account;

import com.frostedmc.core.utils.ColorCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public enum Rank {

    PLAYER(0, "", ColorCode.GRAY, 0),
    VIP(1, "VIP ", ColorCode.GOLD, 1),
    ELITE(2, "ELITE ", ColorCode.YELLOW, 2),
    LEGEND(3, "LEGEND ", ColorCode.DARK_PURPLE, 3),
    YOUTUBE(4, "YOUTUBE ", ColorCode.RED, 4),
    TWITCH(5, "TWITCH ", ColorCode.DARK_PURPLE, 5),
    BUILDER(6, "BUILDER ", ColorCode.LIGHT_PURPLE, 6),
    HELPER(7, "HELPER ", ColorCode.DARK_AQUA, 7),
    MOD(8, "MOD ", ColorCode.BLUE, 8),
    ADMIN(9, "ADMIN ", ColorCode.RED, 10),
    DEV(10, "DEV ", ColorCode.YELLOW, 9),
    OWNER(11, "OWNER ", ColorCode.DARK_RED, 11);

    private int id;
    private String prefix;
    private ColorCode colorCode;
    private int level;

    private Rank(int id, String prefix, ColorCode colorCode, int level) {
        this.id = id;
        this.prefix = prefix;
        this.colorCode = colorCode;
        this.level = level;
    }

    public int getID() {
        return this.id;
    }

    public String getPrefix(boolean space) {
        String additional = "";

        if(this.id > 0) {
            additional = "" + ColorCode.BOLD.convert();
        }

        if(space) {
            return this.colorCode.convert() + additional + this.prefix + ColorCode.GRAY.convert();
        } else {
            return new String(this.colorCode.convert() + additional + this.prefix).replace(" ", "") + ColorCode.GRAY.convert();
        }
    }

    public int getLevel() {
        return this.level;
    }

    public static boolean compare(Rank rank, Rank required) {
        return rank.getLevel() >= required.getLevel();
    }

    public static boolean compare(Rank rank, Rank required, Rank[] allowedRanks) {
        List<Rank> list = new ArrayList<Rank>();

        for(Rank allowedRank : allowedRanks) {
            list.add(allowedRank);
        }

        if(list.contains(rank)) {
            return true;
        } else {
            return rank.getLevel() >= required.getLevel();
        }
    }

    public static Rank parse(int id) {
        for(Rank rank : Rank.values()) {
            if(rank.getID() == id) {
                return rank;
            }
        }

        return null;
    }
}
