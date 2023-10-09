package com.frostedmc.kingdoms.backend;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class PlayerData {

    private UUID uuid;
    private int gold;
    private Kingdom currentKingdom;

    PlayerData(UUID uuid, int gold, Kingdom currentKingdom) {
        this.uuid = uuid;
        this.gold = gold;
        this.currentKingdom = currentKingdom;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getGold() {
        return this.gold;
    }

    public Kingdom getCurrentKingdom() {
        return this.currentKingdom;
    }

    public boolean inKingdom() {
        return this.currentKingdom != null;
    }
}
