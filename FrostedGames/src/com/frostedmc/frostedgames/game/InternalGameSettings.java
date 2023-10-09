package com.frostedmc.frostedgames.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class InternalGameSettings {

    public static com.frostedmc.frostedgames.game.Map map;
    public static int minPlayers = 2;
    public static int maxPlayers = 25;

    public static int countdown = 30;
    public static Status status = Status.WAITING;
    public static boolean deathmatch = false;

    public static Map<Player, District> districtMap = new HashMap<Player, District>();
    public static Map<Player, Integer> kills = new HashMap<Player, Integer>();
    public static List<Player> rewards = new ArrayList<Player>();
    public static Player gameMaker = null;
    public static int peace = 30;
    public static int ingameMinutes = 20;

    public static boolean enablePeace = true;
}