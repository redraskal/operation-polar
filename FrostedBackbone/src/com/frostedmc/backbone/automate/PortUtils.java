package com.frostedmc.backbone.automate;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Redraskal_2 on 11/6/2016.
 */
public class PortUtils {

    public static boolean checkName(String string) {
        boolean ok = false;
        ok = string.matches("\\w+");
        if(string.contains("-") && !string.contains(" ")) {
            ok = true;
        }
        return ok;
    }

    public static boolean isPortOpen(int n) {
        boolean bl;
        block12 : {
            bl = false;
            ServerSocket serverSocket = null;
            try {
                try {
                    serverSocket = new ServerSocket(n);
                    bl = true;
                }
                catch (IOException var3_3) {
                    if (serverSocket == null || serverSocket.isClosed()) break block12;
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                if (!(serverSocket == null || serverSocket.isClosed())) {
                    try {
                        serverSocket.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bl;
    }
}