package com.frostedmc.frostedgames.game;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public enum Status {

    WAITING(0),
    STARTING(1),
    INGAME(2),
    ENDED(3);

    private int id;

    private Status(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}