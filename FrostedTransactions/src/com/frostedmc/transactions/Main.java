package com.frostedmc.transactions;

import com.frostedmc.core.Core;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.sql.SQLDetails;
import com.frostedmc.transactions.modules.KickModule;
import com.frostedmc.transactions.modules.VotifierModule;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() { return instance; }

    private Jedis jedis;

    public void onEnable() {
        instance = this;
        Core.initialize(new SQLDetails("localhost", 3306, "root", "jKdm8328bekUgefbJH23y", "serverWide"), this.getLogger(), new Module[]{
                new KickModule(this)
        });
        this.getLogger().info("[Redis] Connecting to server...");
        jedis = new Jedis("127.0.0.1");
        this.getLogger().info("[Redis] Authenticating...");
        jedis.auth("M30WC4TSARECUTE9394399491348U4TR98GWERUH85PETRHOTUREIRUSGYUEYUKW1I3574BQRI6EK");
        this.getLogger().info("[Redis] Ready to publish requests.");

        Core.getInstance().enableModule(new VotifierModule(this));
        this.getCommand("trans").setExecutor(new TransCommand());
    }

    public void publish(String channel, String message) {
        this.getLogger().info("[Redis] Publishing [" + message + "] to " + channel + ".");
        jedis.publish(channel, message);
    }

    public void onDisable() {
        Core.getInstance().disable();
        this.getLogger().info("[Redis] Disconnecting from server...");
        jedis.quit();
    }
}