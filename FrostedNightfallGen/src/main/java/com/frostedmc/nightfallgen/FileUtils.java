package com.frostedmc.nightfallgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class FileUtils {

    /**
     * Quickly copies a Directory.
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void quickCopy(File source, File dest) throws IOException {
        dest.mkdirs();
        for(File file : source.listFiles()) {
            if(file.isDirectory()) {
                file.mkdirs();
                quickCopy(new File(source, file.getName() + "/"),
                        new File(dest, file.getName() + "/"));
            } else {
                quickCopyFile(file, new File(dest, file.getName()));
            }
        }
    }

    /**
     * Quickly deletes a Directory.
     * @param directory
     * @throws IOException
     */
    public static void quickDelete(File directory) throws IOException {
        for(File file : directory.listFiles()) {
            if(file.isDirectory()) {
                quickDelete(new File(directory, file.getName() + "/"));
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

    /**
     * Quickly copies a File.
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void quickCopyFile(File source, File dest) throws IOException {
        FileChannel inputChannel = new FileInputStream(source).getChannel();
        FileChannel outputChannel = new FileOutputStream(dest).getChannel();

        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        inputChannel.close();
        outputChannel.close();
    }
}