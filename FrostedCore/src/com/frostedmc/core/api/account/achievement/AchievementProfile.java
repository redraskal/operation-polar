package com.frostedmc.core.api.account.achievement;

import com.frostedmc.core.api.account.Timestamp;

import java.util.Map;
import java.util.UUID;

public class AchievementProfile {

    private UUID player;
    private Map<Achievement, Timestamp> achievementList;

    public AchievementProfile(UUID player, Map<Achievement, Timestamp> achievementList) {
        this.player = player;
        this.achievementList = achievementList;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public Achievement[] getAchievements() {
        return this.achievementList.values().toArray(new Achievement[this.achievementList.size()]);
    }

    public boolean contains(String title) {
        for(Map.Entry<Achievement, Timestamp> entry : achievementList.entrySet()) {
            if(entry.getKey().getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    public Timestamp getTimestamp(String achievement) {
        for(Map.Entry<Achievement, Timestamp> entry : achievementList.entrySet()) {
            if(entry.getKey().getTitle().equalsIgnoreCase(achievement)) {
                return entry.getValue();
            }
        }
        return Timestamp.getCurrentTimestamp();
    }
}