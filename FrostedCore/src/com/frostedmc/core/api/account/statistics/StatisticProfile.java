package com.frostedmc.core.api.account.statistics;

import com.frostedmc.core.api.account.Timestamp;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/26/2017.
 */
public class StatisticProfile {

    private UUID player;
    private Map<String, Double> stat;
    private Map<String, Timestamp> lastReset;

    public StatisticProfile(UUID player, Map<String, Double> stat, Map<String, Timestamp> lastReset) {
        this.player = player;
        this.stat = stat;
        this.lastReset = lastReset;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public Map<String, Double> getArray() {
        return this.stat;
    }

    public double getStatistic(String name) {
        if(stat.containsKey(name)) {
            return stat.get(name);
        } else {
            return 0;
        }
    }

    public Timestamp getLastUpdate(String name) {
        if(lastReset.containsKey(name)) {
            return lastReset.get(name);
        } else {
            return Timestamp.getCurrentTimestamp();
        }
    }
}