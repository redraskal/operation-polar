package com.frostedmc.backbone.automate;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.backbone.modules.JoinModule;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 12/30/2016.
 */
public class ServerManager {

    private static ServerManager instance = null;

    public static ServerManager getInstance() {
        if(instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    private File serverFolder;
    private List<ServerInfo> serverInfoList = new LinkedList<ServerInfo>();
    private File[] scripts;
    private File serverJar;

    private ServerManager() {
        this.serverFolder = new File(Backbone.getInstance().getDataFolder().getAbsoluteFile()
                .getParentFile().getParentFile().getParentFile().getPath() + "/onlservers/");
        this.scripts = new File[]{
                new File(this.serverFolder.getAbsoluteFile().getParentFile().getPath() + File.separator + "start-screen.sh"),
                new File(this.serverFolder.getAbsoluteFile().getParentFile().getPath() + File.separator + "stop-screen.sh")
        };
        this.serverJar = new File(TemplateManager.getInstance().getTemplateFolder().getAbsolutePath() + File.separator + "spigot.jar");
    }

    public void autoStartServers() {
        for(File currentServers : this.getServerFolder().listFiles()) {
            if(!currentServers.isDirectory()) continue;
            for(File serverDirectory : currentServers.listFiles()) {
                if(!serverDirectory.isDirectory()) continue;
                if(TemplateManager.getInstance().getTemplate(serverDirectory.getName().split("-")[0]) == null) continue;
                ServerInfo serverInfo = new ServerInfo(TemplateManager.getInstance().getTemplate(serverDirectory.getName().split("-")[0]),
                        serverDirectory, this.nextPort());
                this.serverInfoList.add(serverInfo);
                ProxyServer.getInstance().getServers().put(serverInfo.getServerName(), ProxyServer.getInstance().constructServerInfo(
                        serverInfo.getServerName(), new InetSocketAddress(serverInfo.getAddress(), serverInfo.getPort()), "Error-"+ UUID.randomUUID().toString(), false));
                this.startServer(serverInfo);
            }
        }
    }

    public File getServerFolder() {
        return this.serverFolder;
    }

    public LinkedList<ServerInfo> getServers(boolean state) {
        LinkedList<ServerInfo> temp = new LinkedList<ServerInfo>();
        for(ServerInfo serverInfo : serverInfoList) {
            if(serverInfo.isStarted() == state)
                temp.add(serverInfo);
        }
        return temp;
    }

    public LinkedList<ServerInfo> getServers(TemplateInfo templateInfo) {
        LinkedList<ServerInfo> temp = new LinkedList<ServerInfo>();
        for(ServerInfo serverInfo : serverInfoList) {
            if(serverInfo.getTemplateInfo() == templateInfo)
                temp.add(serverInfo);
        }
        return temp;
    }

    public LinkedList<ServerInfo> getServers(TemplateInfo templateInfo, boolean state) {
        LinkedList<ServerInfo> temp = new LinkedList<ServerInfo>();
        for(ServerInfo serverInfo : serverInfoList) {
            if(serverInfo.getTemplateInfo() == templateInfo && serverInfo.isStarted() == state)
                temp.add(serverInfo);
        }
        return temp;
    }

    public ServerInfo getServer(String name) {
        for(ServerInfo serverInfo : serverInfoList) {
            if(serverInfo.getServerName().equalsIgnoreCase(name))
                return serverInfo;
        }
        return null;
    }

    public boolean doesServerExist(String name) {
        return getServer(name) != null;
    }

    public int nextPort() {
        int next = 25566;
        for(ServerInfo serverInfo : this.serverInfoList) {
            if(serverInfo.getPort() >= next)
                next=serverInfo.getPort()+1;
        }
        return next;
    }

    public ServerInfo createServer(TemplateInfo templateInfo, String name) {
        if(doesServerExist(name)) return null;
        if(RedisBungee.getApi().getAllServers().contains(name)) return null;
        File serverFolder = new File(this.getServerFolder().getPath() + "/" + templateInfo.getName() + "/" + name + "/");
        if(!serverFolder.exists()) {
            serverFolder.mkdirs();
        }
        for(File toCopy : TemplateManager.getInstance().getTemplateFolder().listFiles()) {
            if(toCopy.isDirectory()) continue;
            FileUtils.copyFile(toCopy, new File(serverFolder.getPath() + "/" + toCopy.getName()));
        }
        ServerInfo serverInfo = new ServerInfo(templateInfo, serverFolder, this.nextPort());
        try {
            serverInfo.modifyProperties();
        } catch (Exception e) { e.printStackTrace(); }
        FileUtils.copyDirectory(templateInfo.getDirectory(), serverFolder);
        this.serverInfoList.add(serverInfo);
        ProxyServer.getInstance().getServers().put(serverInfo.getServerName(), ProxyServer.getInstance().constructServerInfo(
            serverInfo.getServerName(), new InetSocketAddress(serverInfo.getAddress(), serverInfo.getPort()), "Error-"+UUID.randomUUID().toString(), false));
        return serverInfo;
    }

    public void deleteServer(ServerInfo serverInfo) {
        ProxyServer.getInstance().getServers().remove(serverInfo.getServerName());
        FileUtils.deleteDirectory(serverInfo.getServerFolder());
        if(serverInfoList.contains(serverInfo))
            serverInfoList.remove(serverInfo);
    }

    public synchronized void startServer(ServerInfo serverInfo) {
        TemplateInfo templateInfo = serverInfo.getTemplateInfo();
        final String[] scriptArguments = new String[]{
                "sh", scripts[0].getPath(), serverInfo.getServerName(),
                String.valueOf(templateInfo.getXmx()) + "M", String.valueOf(templateInfo.getXms()) + "M",
                serverInfo.getServerFolder().getAbsolutePath(), serverInfo.getServerFolder().getAbsolutePath() + File.separator + "spigot.jar"};
        ProxyServer.getInstance().getScheduler().runAsync(Backbone.getInstance(), new Runnable() {
            public void run() {
                try {
                    RedisBungee.getApi().sendChannelMessage("playpen-create", ChatColor.translateAlternateColorCodes('&',
                            "&e" + serverInfo.getServerName() + " &7has connected to the network!\n&3"
                                    + serverInfo.getServerName() + " &a<-> &7"
                                    + Backbone.getInstance().getBoxName() + " / PORT - " + serverInfo.getPort()));
                    Process process = Runtime.getRuntime().exec(scriptArguments);
                    process.waitFor();
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    public synchronized void stopServer(ServerInfo serverInfo) {
        TemplateInfo templateInfo = serverInfo.getTemplateInfo();
        final String[] scriptArguments = new String[]{
                "sh", scripts[1].getPath(), serverInfo.getServerName()};
        net.md_5.bungee.api.config.ServerInfo toChoose = JoinModule.randomThinking();
        for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getServerInfo(serverInfo.getServerName()).getPlayers()) {
            if(toChoose != null) {
                proxiedPlayer.connect(toChoose);
            }
        }
        ProxyServer.getInstance().getScheduler().runAsync(Backbone.getInstance(), new Runnable() {
            public void run() {
                try {
                    RedisBungee.getApi().sendChannelMessage("playpen-create", ChatColor.translateAlternateColorCodes('&',
                            "&e" + serverInfo.getServerName() + " &7has disconnected from the network!\n&3"
                                    + serverInfo.getServerName() + " &c<-> &7"
                                    + Backbone.getInstance().getBoxName() + " / PORT - " + serverInfo.getPort()));
                    Process process = Runtime.getRuntime().exec(scriptArguments);
                    process.waitFor();
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}