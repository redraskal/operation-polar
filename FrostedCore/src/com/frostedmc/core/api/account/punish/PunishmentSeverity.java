package com.frostedmc.core.api.account.punish;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public enum PunishmentSeverity {

    SEVERITY_1(1),
    SEVERITY_2(2),
    SEVERITY_3(3),
    SEVERITY_4(4),
    SEVERITY_5(5);

    private int id;

    private PunishmentSeverity(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static PunishmentSeverity byID(int id) {
        for(PunishmentSeverity punishmentSeverity : PunishmentSeverity.values()) {
            if(punishmentSeverity.getID() == id) {
                return punishmentSeverity;
            }
        }
        return null;
    }
}