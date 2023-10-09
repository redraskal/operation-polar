package com.frostedmc.arenapvp.listener;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.arenapvp.arena.elo.EloManager;
import com.frostedmc.arenapvp.arena.elo.EloProfile;
import com.frostedmc.arenapvp.arena.elo.EloResetRunnable;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import me.redraskal.Glacier.check.SpectateEvent;
import me.redraskal.Glacier.check.ViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().teleport(ArenaPVP.getInstance().LOBBY_SPAWN);
        JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/HubChristmas.mp3");
        EloProfile eloProfile = EloManager.getInstance().fetchProfile(event.getPlayer().getUniqueId());
        for(Kit kit : Kit.values()) {
            if(eloProfile.getElo(kit) == -1) {
                if(!EloResetRunnable.checkReset(event.getPlayer().getUniqueId(), kit)) {
                    new EloResetRunnable(event.getPlayer(), kit).runTaskTimer(ArenaPVP.getInstance(), 0, 20L);
                }
            }
        }
    }

    @EventHandler
    public void onViolation(ViolationEvent event) {
        if(event.getViolation().getCheck().getName().equalsIgnoreCase("AntiKnockback")) {
            if(!ArenaManager.getInstance().inArena(event.getViolation().getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGlacierSpectate(SpectateEvent event) {
        if(ArenaManager.getInstance().inArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}