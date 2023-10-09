package com.frostedmc.hub.manager;

import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;

public class CustomPubSub extends JedisPubSub {

    private static Map<String, Integer> cache = new HashMap<String, Integer>();

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equalsIgnoreCase("server-cat-status")) {
            cache.put(message.split(":")[0], Integer.parseInt(message.split(":")[1]));
        }
    }

    public static int getPlayers(String category) {
        if(cache.containsKey(category)) {
            return cache.get(category);
        } else {
            return 0;
        }
    }
}