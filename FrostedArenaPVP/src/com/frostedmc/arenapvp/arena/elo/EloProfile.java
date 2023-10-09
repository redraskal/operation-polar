package com.frostedmc.arenapvp.arena.elo;

import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.core.api.account.Timestamp;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 12/31/2016.
 */
public class EloProfile {

    private UUID player;
    private Map<Kit, Integer> elo;
    private Map<Kit, Timestamp> lastReset;

    public EloProfile(UUID player, Map<Kit, Integer> elo, Map<Kit, Timestamp> lastReset) {
        this.player = player;
        this.elo = elo;
        this.lastReset = lastReset;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public int getElo(Kit kit) {
        if(elo.containsKey(kit)) {
            return elo.get(kit);
        } else {
            return 250;
        }
    }

    public Timestamp getLastReset(Kit kit) {
        if(lastReset.containsKey(kit)) {
            return lastReset.get(kit);
        } else {
            return Timestamp.getCurrentTimestamp();
        }
    }
}