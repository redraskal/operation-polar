package com.frostedmc.gamemanager.api;

/**
 * Created by Redraskal_2 on 2/24/2017.
 */
public class GameFlags {

    public int minPlayers = 2;
    public int maxPlayers = 24;
    public boolean forceStart = false;
    public int startCountdown = 30;
    public boolean generateChunks = false;

    public boolean enablePVP = true;
    public boolean enablePVE = true;
    public boolean enablePVM = true;

    public boolean bloodEffects = true;
    public boolean dropItemsOnDeath = true;
    public boolean dropArmorOnDeath = true;

    public boolean allowFoodLevelChange = false;
    public boolean allowWeatherChange = false;
    public boolean allowItemPickup = false;
    public boolean allowItemDrop = false;
    public boolean allowInventoryClick = false;
    public boolean allowInteract = false;

    public boolean enableExplosions = false;
    public boolean instaKillExplosions = false;
}