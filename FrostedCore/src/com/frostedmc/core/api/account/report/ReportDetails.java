package com.frostedmc.core.api.account.report;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class ReportDetails {

    private UUID player;
    private UUID reporter;
    private Map<ReportCategory, String> reasons;

    public ReportDetails(UUID player, UUID reporter, Map<ReportCategory, String> reasons) {
        this.player = player;
        this.reporter = reporter;
        this.reasons = reasons;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public UUID getReporter() {
        return this.reporter;
    }

    public Map<ReportCategory, String> getReasons() {
        return this.reasons;
    }
}