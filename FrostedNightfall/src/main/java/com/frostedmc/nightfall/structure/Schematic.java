package com.frostedmc.nightfall.structure;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Schematic {

     @Getter private final int width;
     @Getter private final int length;
     @Getter private final int height;
     @Getter private final byte[] blocks;
     @Getter private final byte[] data;

     private Schematic(int width, int length, int height, byte[] blocks, byte[] data) {
         this.width = width;
         this.length = length;
         this.height = height;
         this.blocks = blocks;
         this.data = data;
     }

    /**
     * Loads a schematic from a file.
     * @param file
     * @return
     * @throws Exception
     */
    public static Schematic load(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        NBTTagCompound nbtdata = NBTCompressedStreamTools.a(fis);
        fis.close();

        short width = nbtdata.getShort("Width");
        short height = nbtdata.getShort("Height");
        short length = nbtdata.getShort("Length");
        byte[] blocks = nbtdata.getByteArray("Blocks");
        byte[] data = nbtdata.getByteArray("Data");

        return new Schematic(width, length, height, blocks, data);
    }
}