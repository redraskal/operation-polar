package com.frostedmc.kingdoms.event;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class ChunkChangeEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private Player player;
    private Chunk from;
    private Chunk to;

    /**
     * Called when a player moves into a different chunk
     * @param player
     * @param from
     * @param to
     */
    public ChunkChangeEvent(Player player, Chunk from, Chunk to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public Player getPlayer() { return this.player; }

    public Chunk getFrom() { return this.from; }

    public Chunk getTo() { return this.to; }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
