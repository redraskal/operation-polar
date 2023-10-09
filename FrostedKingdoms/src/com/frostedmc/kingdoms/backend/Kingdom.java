package com.frostedmc.kingdoms.backend;

import org.bukkit.Chunk;

import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class Kingdom {

    private UUID uuid;
    private String name;
    private int resourcePoints;
    private List<Chunk> ownedChunks;
    private List<UUID> members;

    Kingdom(UUID uuid, String name, int resourcePoints, List<Chunk> ownedChunks, List<UUID> members) {
        this.uuid = uuid;
        this.name = name;
        this.resourcePoints = resourcePoints;
        this.ownedChunks = ownedChunks;
        this.members = members;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getResourcePoints() {
        return this.resourcePoints;
    }

    public List<Chunk> getChunks() {
        return this.ownedChunks;
    }

    public List<UUID> getMembers() {
        return this.members;
    }
}
