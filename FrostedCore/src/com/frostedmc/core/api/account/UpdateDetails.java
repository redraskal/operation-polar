package com.frostedmc.core.api.account;

/**
 * Created by Redraskal_2 on 8/27/2016.
 */
public class UpdateDetails {

    public enum UpdateType {
        USERNAME(0, "username"),
        RANK(1, "rank"),
        ICICLES(1, "icicles"),
        LAST_SEEN(0, "lastseen"),
        TIME_ONLINE(2, "timeonline"),
        IP_ADDRESS(0, "ip");

        private int type;
        private String column;

        private UpdateType(int type, String column) {
            this.type = type;
            this.column = column;
        }

        public int getType() {
            return this.type;
        }

        public String getColumn() {
            return this.column;
        }
    }

    private UpdateType updateType;
    private Object value;

    public UpdateDetails(UpdateType updateType, Object value) {
        this.updateType = updateType;
        this.value = value;
    }

    public UpdateType getUpdateType() {
        return this.updateType;
    }

    public Object getValue() {
        return this.value;
    }
}
