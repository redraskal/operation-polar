package com.frostedmc.hub.manager;

public class GameStatus {

    private String server;
    private int onlinePlayers;
    private int maxPlayers;
    private int gameStatus;
    private String game;

    public GameStatus(String server, int onlinePlayers, int maxPlayers, int gameStatus, String game) {
        this.server = server;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.gameStatus = gameStatus;
        this.game = game;
    }

    public String getServer() {
        return this.server;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getGameStatus() {
        return this.gameStatus;
    }

    public String getGame() {
        return this.game;
    }
}