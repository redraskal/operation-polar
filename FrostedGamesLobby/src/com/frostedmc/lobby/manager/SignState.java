package com.frostedmc.lobby.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public enum SignState {

    WAITING(0, "Recruiting"),
    STARTING(1, "Starting"),
    INGAME(2, "Spectate"),
    RESTARTING(3, "Restarting");

    private int id;
    private String tag;

    private SignState(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public int getID() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public void update(Sign sign, String serverInfo) {
        String[] array = serverInfo.split(",");
        if(this.id == 3) {
            sign.setLine(0, "[---]");
        } else {
            sign.setLine(0, "[" + array[0] + "]");
        }
        sign.setLine(1, fromID(this.id).getTag());
        if(this.id == 3) {
            sign.setLine(2, "---");
            sign.setLine(3, "0/25");
        } else {
            sign.setLine(2, array[3]);
            sign.setLine(3, array[1] + "/25");
        }
        String[] lines = sign.getLines();
        sign.update();
        sign.getWorld().getPlayers().stream().filter((p) -> {
            return p.getLocation().distanceSquared(sign.getLocation()) <= 65;
        }).forEach(p -> p.sendSignChange(sign.getLocation(), lines));
        Block behind = SignManager.getInstance().fetchBehind(sign);
        behind.setType(Material.STAINED_CLAY);
        if(this.id == 0) {
            behind.setData((byte) 5);
        }
        if(this.id == 1) {
            behind.setData((byte) 13);
        }
        if(this.id == 2) {
            behind.setData((byte) 11);
        }
        if(this.id == 3) {
            behind.setData((byte) 4);
        }
    }

    public void updateDefault(Sign sign) {
        this.update(sign, "---,0," + this.id + ",---");
    }

    public static SignState fromID(int id) {
        for(SignState signState : SignState.values()) {
            if(signState.getID() == id) {
                return signState;
            }
        }
        return null;
    }

    public static SignState parse(String serverInfo) {
        String[] array = serverInfo.split(",");
        for(SignState signState : SignState.values()) {
            if(signState.getID() == Integer.parseInt(array[2])) {
                return signState;
            }
        }
        return SignState.RESTARTING;
    }
}