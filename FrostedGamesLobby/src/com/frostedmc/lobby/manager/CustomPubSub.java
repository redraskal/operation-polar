package com.frostedmc.lobby.manager;

import redis.clients.jedis.JedisPubSub;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class CustomPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equalsIgnoreCase("fg-server")) {
            if(SignManager.getInstance() != null) {
                try {
                    SignManager.getInstance().handlePubSub(message);
                } catch (Exception e) {}
            }
        }
    }
}