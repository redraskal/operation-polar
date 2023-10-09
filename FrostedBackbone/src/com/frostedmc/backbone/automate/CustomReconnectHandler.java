package com.frostedmc.backbone.automate;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Redraskal_2 on 12/31/2016.
 */
public class CustomReconnectHandler implements ReconnectHandler {

    @Override
    public net.md_5.bungee.api.config.ServerInfo getServer(ProxiedPlayer player)
    {
        net.md_5.bungee.api.config.ServerInfo server = null;
        if(onlineServers()) {
            //TODO
        } else {
            if(ProxyServer.getInstance().getServerInfo("Thinking-1") != null) {
                server = ProxyServer.getInstance().getServerInfo("Thinking-1");
            }
        }
        Preconditions.checkState(server != null, "Default server not defined");
        return server;
    }

    @Override
    public void setServer(ProxiedPlayer proxiedPlayer) {

    }

    @Override
    public void save() {

    }

    @Override
    public void close() {

    }

    private boolean onlineServers() {
        for(net.md_5.bungee.api.config.ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if(!serverInfo.getName().contains("Thinking")) {
                return true;
            }
        }
        return false;
    }

    public static net.md_5.bungee.api.config.ServerInfo getForcedHost(PendingConnection con)
    {
        if ( con.getVirtualHost() == null )
        {
            return null;
        }

        String forced = con.getListener().getForcedHosts().get( con.getVirtualHost().getHostString() );

        if ( forced == null && con.getListener().isForceDefault() )
        {
            forced = con.getListener().getDefaultServer();
        }
        return ProxyServer.getInstance().getServerInfo( forced );
    }
}