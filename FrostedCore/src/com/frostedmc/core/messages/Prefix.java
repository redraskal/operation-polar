package com.frostedmc.core.messages;

import com.frostedmc.core.utils.ColorCode;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public enum Prefix {

    NETWORK("Network", PrefixType.NETWORK),
    SERVER("Server", PrefixType.NETWORK),
    FIND("Find", PrefixType.NETWORK),
    CLOUD("Cloud", PrefixType.NETWORK),
    PARTY("Party", PrefixType.OTHER),
    CHAT("Chat", PrefixType.NETWORK),
    PING("Ping", PrefixType.NETWORK),
    COMMAND("Command", PrefixType.OTHER),
    COMMANDS("Commands", PrefixType.OTHER),
    GLACIER("Glacier", PrefixType.ADVERT),
    RANK("Rank", PrefixType.OTHER),
    ACCOUNT("Account", PrefixType.OTHER),
    ICICLES("Icicles", PrefixType.OTHER),
    GAME("Game", PrefixType.OTHER),
    TELEPORT("Teleport", PrefixType.OTHER),
    GAMEMODE("Gamemode", PrefixType.OTHER),
    KINGDOMS("Kingdoms", PrefixType.NETWORK),
    MESSAGE("Message", PrefixType.OTHER),
    REPORT("Report", PrefixType.OTHER),
    RELOAD("Reload", PrefixType.NETWORK),
    FROSTY("Frosty", PrefixType.OTHER),
    PUNISH("Punish", PrefixType.NETWORK),
    FUN("Fun", PrefixType.OTHER),
    COSMETICS("Cosmetics", PrefixType.NETWORK),
    MODULE("Module", PrefixType.NETWORK),
    JOIN("Join", PrefixType.OTHER),
    LEAVE("Leave", PrefixType.OTHER),
    NONE("", PrefixType.ADVERT);

    public String title;
    public PrefixType prefixType;

    private Prefix(String title, PrefixType prefixType) {
        this.title = title;
        this.prefixType = prefixType;
    }

    public String getTitle() {
        return this.title;
    }

    public PrefixType getPrefixType() {
        return this.prefixType;
    }

    public String build() {
        if(this == Prefix.NONE) {
            return "" + prefixType.getChatColor();
        } else {
            return prefixType.getChatColor() + title + "> " + ColorCode.GRAY.convert();
        }
    }
}