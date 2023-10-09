package com.frostedmc.core.api.account;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public class AccountDetails {

    private UUID uuid;
    private String username;
    private Rank rank;
    private int icicles;

    private Timestamp firstJoined;
    private Timestamp lastLogin;
    private double timeOnline;
    private String ipAddress;

    AccountDetails(UUID uuid, String username, Rank rank, int icicles, Timestamp firstJoined, Timestamp lastLogin, double timeOnline, String ipAddress) {
        this.uuid = uuid;
        this.username = username;
        this.rank = rank;
        this.icicles = icicles;

        this.firstJoined = firstJoined;
        this.lastLogin = lastLogin;
        this.timeOnline = timeOnline;
        this.ipAddress = ipAddress;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public Rank getRank() {
        return this.rank;
    }

    public int getIcicles() {
        return this.icicles;
    }

    public Timestamp getFirstJoined() { return this.firstJoined; }

    public Timestamp getLastLogin() { return this.lastLogin; }

    public String getIpAddress() { return this.ipAddress; }

    public double getMinutesOnline() { return this.timeOnline; }
}