package com.frostedmc.backbone.automate;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class ServerInfo {

    private TemplateInfo templateInfo;
    private File serverFolder;
    private int port;

    ServerInfo(TemplateInfo templateInfo, File serverFolder, int port) {
        this.templateInfo = templateInfo;
        this.serverFolder = serverFolder;
        this.port = port;
    }

    public TemplateInfo getTemplateInfo() {
        return this.templateInfo;
    }

    public File getServerFolder() {
        return this.serverFolder;
    }

    public String getServerName() {
        return this.serverFolder.getName();
    }

    public String getAddress() {
        return "127.0.0.1";
    }

    public int getPort() {
        return this.port;
    }

    public void modifyProperties() throws Exception {
        File properties = new File(this.serverFolder.getPath() + "/server.properties");
        BufferedReader fileReader = new BufferedReader(new FileReader(properties));
        StringBuilder builder = new StringBuilder();
        String string;
        while((string = fileReader.readLine()) != null) {
            if(!builder.toString().isEmpty())
                builder.append("\n");
            builder.append(string);
        }
        fileReader.close();
        string = builder.toString();
        string = string.replace("$PORT", "" + this.port);
        string = string.replace("$NAME", this.getServerName());
        FileWriter fileWriter = new FileWriter(properties);
        fileWriter.write(string);
        fileWriter.close();
    }

    public synchronized boolean isStarted() {
        try {
            Socket localSocket = new Socket();
            localSocket.connect(new InetSocketAddress(getAddress(), this.port), 1000);
            if (!localSocket.isConnected()) {
                localSocket.close();
                return false;
            }
            localSocket.setSoTimeout(2500);
            OutputStream localOutputStream = localSocket.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            InputStream localInputStream = localSocket.getInputStream();
            InputStreamReader localInputStreamReader = new InputStreamReader(localInputStream, Charset.forName("UTF-16BE"));
            localDataOutputStream.write(new byte[] { -2, 1 });
            int i = localInputStreamReader.read();
            char[] arrayOfChar = new char[i];
            String str = new String(arrayOfChar);
            String[] arrayOfString = str.split("");
            localSocket.close();
            return arrayOfString[3] != null;
        } catch (Exception e) { return false; }
    }
}
