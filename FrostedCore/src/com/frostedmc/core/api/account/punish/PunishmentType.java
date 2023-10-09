package com.frostedmc.core.api.account.punish;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public enum PunishmentType {

    WARN(0),
    TEMP_MUTE(1),
    PERM_MUTE(2),
    TEMP_BAN(3),
    PERM_BAN(4);

    private int id;

    private PunishmentType(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static PunishmentType byID(int id) {
        for(PunishmentType punishmentType : PunishmentType.values()) {
            if(punishmentType.getID() == id) {
                return punishmentType;
            }
        }
        return null;
    }
}