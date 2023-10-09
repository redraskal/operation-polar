package com.frostedmc.transactions.modules;

import com.frostedmc.core.module.Module;
import com.frostedmc.transactions.Main;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class VotifierModule extends Module implements Listener {

    private JavaPlugin javaPlugin;

    public VotifierModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "Votifier";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPreLogin(VotifierEvent event) {
        Vote vote = event.getVote();
        //TODO: Save vote
        Main.getInstance().publish("transactions-vote", vote.getUsername());
    }
}