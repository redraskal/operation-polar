package com.frostedmc.gamemanager;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Redraskal_2 on 3/18/2017.
 */
public class BlankGenerator extends ChunkGenerator {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random,
                                          int chunkx, int chunkz, ChunkGenerator.BiomeGrid biomes) {
        return new byte[world.getMaxHeight()/16][];
    }
}