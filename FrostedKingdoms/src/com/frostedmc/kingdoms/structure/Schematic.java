package com.frostedmc.kingdoms.structure;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Redraskal_2 on 11/13/2016.
 */
public class Schematic {

     private int width;
     private int length;
     private int height;
     private byte[] blocks;
     private byte[] data;

     private Schematic(int width, int length, int height, byte[] blocks, byte[] data) {
         this.width = width;
         this.length = length;
         this.height = height;
         this.blocks = blocks;
         this.data = data;
     }

     public int getWidth() {
         return width;
     }

     public int getLength() {
         return length;
     }

     public int getHeight() {
         return height;
     }

     public byte[] getBlocks() {
         return blocks;
     }

     public byte[] getData() {
         return data;
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