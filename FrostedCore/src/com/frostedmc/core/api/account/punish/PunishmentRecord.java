package com.frostedmc.core.api.account.punish;

import com.frostedmc.core.api.account.Timestamp;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class PunishmentRecord {

    private UUID uuid;
    private PunishmentType punishmentType;
    private PunishmentSeverity punishmentSeverity;
    private Timestamp issued;
    private Timestamp end;
    private UUID punisher;
    private String reason;

    public PunishmentRecord(UUID uuid, PunishmentType punishmentType,
                            PunishmentSeverity punishmentSeverity, Timestamp issued, Timestamp end,
                            UUID punisher, String reason) {
        this.uuid = uuid;
        this.punishmentType = punishmentType;
        this.punishmentSeverity = punishmentSeverity;
        this.issued = issued;
        this.end = end;
        this.punishmentSeverity = punishmentSeverity;
        this.punisher = punisher;
        this.reason = reason;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public PunishmentType getPunishmentType() {
        return this.punishmentType;
    }

    public PunishmentSeverity getPunishmentSeverity() {
        return this.punishmentSeverity;
    }

    public Timestamp getIssued() {
        return this.issued;
    }

    public Timestamp getEnd() {
        return this.end;
    }

    public UUID getPunisher() {
        return this.punisher;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean ended() {
        if(Timestamp.getCurrentTimestamp().toDate().after(end.toDate()))
            return true;
        return false;
    }
}