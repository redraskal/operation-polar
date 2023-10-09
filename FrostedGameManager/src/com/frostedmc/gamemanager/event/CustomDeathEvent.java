package com.frostedmc.gamemanager.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class CustomDeathEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private Player entity;
    private Entity damager;
    private String cause;

    public CustomDeathEvent(Player entity, Entity damager, String cause) {
        this.entity = entity;
        this.damager = damager;
        this.cause = cause;
    }

    public Player getEntity() {
        return this.entity;
    }

    public Entity getDamager() {
        return this.damager;
    }

    public String getCause() {
        return this.cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}