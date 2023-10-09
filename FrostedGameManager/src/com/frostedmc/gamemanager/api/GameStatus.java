package com.frostedmc.gamemanager.api;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public enum GameStatus {

    WAITING(0),
    STARTING(1),
    INGAME(2),
    ENDED(3);

    private int id;

    private GameStatus(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}