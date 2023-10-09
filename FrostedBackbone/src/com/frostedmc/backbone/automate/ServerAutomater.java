package com.frostedmc.backbone.automate;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.backbone.commands.CloudCommand;
import com.frostedmc.core.Core;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 3/19/2017.
 */
public class ServerAutomater {

    public ServerAutomater() {
        Core.getInstance().getLogger()
                .info("[Automater] Scheduling server automation task...");
        BungeeCord.getInstance().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 0, 10, TimeUnit.SECONDS);
        Core.getInstance().getLogger()
                .info("[Automater] This server will now automatically create/remove servers.");
    }

    private void check() {
        if((System.currentTimeMillis() - CloudCommand.last) <= 1000L) return;
        if(Backbone.getInstance().stopAutomate) return;
        long maxMemory = getMaxMemory();
        long freeMemory = getFreeMemory();
        Core.getInstance().getLogger()
                .info("[Automater] HEARTBEAT: " + freeMemory + "/" + maxMemory + " mb available.");
        if(freeMemory <= 2000L) {
            dump();
            return;
        }
        manage(TemplateManager.getInstance().getTemplate("Hub"));
        manage(TemplateManager.getInstance().getTemplate("Arcade"));
        manage(TemplateManager.getInstance().getTemplate("ArenaPVP"));
        manage(TemplateManager.getInstance().getTemplate("OITC"));
        manage(TemplateManager.getInstance().getTemplate("Hub"));
    }

    private long getMaxMemory() {
        try {
            return memoryInfo().get("MemTotal");
        } catch (Exception e) {
            return 0;
        }
    }

    private long getFreeMemory() {
        try {
            return memoryInfo().get("MemFree");
        } catch (Exception e) {
            return 0;
        }
    }

    private Map<String, Long> memoryInfo() throws Exception {
        Process process = Runtime.getRuntime().exec("cat /proc/meminfo");
        process.waitFor();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        Map<String, Long> temp = new HashMap<String, Long>();
        String line = "";
        while((line = reader.readLine()) != null) {
            line = line.replace("kB", "");
            line = line.replaceAll("\\s+", "");
            temp.put(line.split(":")[0], (Long.valueOf(line.split(":")[1])/1024));
        }
        return temp;
    }

    private void manage(TemplateInfo templateInfo) {
        int network = RedisBungee.getApi().getPlayerCount();
        int size = count(templateInfo);
        if(size == 0) {
            create(templateInfo);
        } else {
            int serversToCreate = 0;
            for(int i=80; i<network; i+=80) {
                if(serversToCreate < 3) {
                    serversToCreate++;
                }
            }
            for(int i=0; i<serversToCreate; i++) {
                create(templateInfo);
            }
        }
    }

    private int count(TemplateInfo templateInfo) {
        return ServerManager.getInstance().getServers(templateInfo).size();
    }

    private void create(TemplateInfo templateInfo) {
        long freeMemory = getFreeMemory();
        if(freeMemory <= 2000L) return;
        if(freeMemory - templateInfo.getXmx() <= 2000L) return;
        int number = 0;
        for(String server : ProxyServer.getInstance().getServers().keySet()) {
            if(server.startsWith(templateInfo.getName() + "-")) {
                try {
                    int temp = Integer.parseInt(server.split("-")[1]);
                    if(temp > number) {
                        number = temp;
                    }
                } catch (Exception e) {}
            }
        }
        number++;
        String name = templateInfo.getName() + "-" + number;
        Core.getInstance().getLogger()
                .info("[Automater] Creating " + name + ".");
        ServerInfo serverInfo = ServerManager.getInstance().createServer(templateInfo, name);
        if(serverInfo != null) {
            ServerManager.getInstance().startServer(serverInfo);
        }
    }

    private void dump() {
        Core.getInstance().getLogger()
                .info("[Automater] Attempting to dump servers not in use to free up memory...");
        for(ServerInfo server : ServerManager.getInstance().getServers(true)) {
            if(ProxyServer.getInstance().getServerInfo(server.getServerName())
                    .getPlayers().size() == 0) {
                if(ServerManager.getInstance().getServers(server.getTemplateInfo()).size() > 1) {
                    boolean delete = true;
                    for(ServerInfo serverInfo : ServerManager.getInstance()
                            .getServers(server.getTemplateInfo())) {
                        if(ProxyServer.getInstance().getServerInfo(server.getServerName())
                                .getPlayers().size() >= 50) {
                            delete = false;
                            break;
                        }
                    }
                    if(delete) {
                        Core.getInstance().getLogger()
                                .info("[Automater] Deleting " + server.getServerName() + " to free up "
                                        + server.getTemplateInfo().getXmx() + " mb of memory.");
                    }
                }
            }
        }
        Core.getInstance().getLogger()
                .info("[Automater] Done.");
    }
}