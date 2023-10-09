package com.frostedmc.hub.manager;

import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class GamePubSub extends JedisPubSub {

    private static Map<String, GameStatus> cache = new HashMap<String, GameStatus>();

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equalsIgnoreCase("server-status")) {
            cache.put(message.split(":")[0], new GameStatus(
                message.split(":")[0],
                Integer.parseInt(message.split(":")[1]),
                Integer.parseInt(message.split(":")[2]),
                Integer.parseInt(message.split(":")[3]),
                message.split(":")[4]
            ));
        }
    }

    public static GameStatus getPlayers(String server) {
        if(cache.containsKey(server)) {
            return cache.get(server);
        } else {
            return new GameStatus(server, 0, 0, 0, "NA");
        }
    }

    public static GameStatus[] getArray(String prefix) {
        List<GameStatus> temp = new ArrayList<GameStatus>();
        for(Map.Entry<String, GameStatus> entry : cache.entrySet()) {
            if(entry.getKey().startsWith(prefix)) {
                temp.add(entry.getValue());
            }
        }
        return temp.toArray(new GameStatus[temp.size()]);
    }
}