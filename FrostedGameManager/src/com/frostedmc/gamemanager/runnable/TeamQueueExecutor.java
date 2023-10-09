package com.frostedmc.gamemanager.runnable;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.manager.TeamManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class TeamQueueExecutor extends BukkitRunnable {

    private final Game game;

    public TeamQueueExecutor(Game game) {
        this.game = game;
        this.runTaskTimer(GameManager.getInstance(), 0, 120L);
    }

    @Override
    public void run() {
        if(!GameManager.getInstance().isGameLoaded(game)
                || GameManager.getInstance().gameStatus.getID() > 1) {
            this.cancel();
        } else {
            try {
                Field f_teamManager = game.getClass().getDeclaredField("teamManager");
                Method m_executeQueue = TeamManager.class.getDeclaredMethod("executeQueue");
                Method m_removeOfflinePlayers = TeamManager.class.getDeclaredMethod("removeOfflinePlayers");

                Object teamManager = f_teamManager.get(game);
                m_removeOfflinePlayers.invoke(teamManager);
                m_executeQueue.invoke(teamManager);
            } catch (Exception e) {
                GameManager.getInstance().getLogger().severe("Could not find teamManager field in " + game.getClass().getName());
            }
        }
    }
}