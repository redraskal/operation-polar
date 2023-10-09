package com.frostedmc.fabrication.game;

/**
 * Created by Redraskal_2 on 10/22/2016.
 */
public enum GameStatus {

    WAITING(0),
    STARTING(1),
    INGAME(2);

    public static GameStatus GLOBAL = GameStatus.WAITING;
    public static boolean JUDGING = false;
    public static int DEFAULT_COUNTDOWN = 10;
    public static int COUNTDOWN = 10;
    public static int DEFAULT_VOTING_COUNTDOWN = 15;
    public static int VOTING_COUNTDOWN = 15;
    public static int MINUTES = 1;
    public static int SECONDS = 15;
    public static int MIN_PLAYERS = 1;
    private int id;

    private GameStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}