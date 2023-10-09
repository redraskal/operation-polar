package com.frostedmc.gamemanager.commands;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class GameCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "game";
    }

    @Override
    public String commandDescription() {
        return "Changes the current game settings.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length == 0) {
            //TODO
        } else {
            if(args[0].equalsIgnoreCase("forcestart")) {
                if(GameManager.getInstance().forceStart()) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &7The match has been force started."));
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &7This command is currently not available."));
                }
                return;
            }
            if(args[0].equalsIgnoreCase("end")) {
                if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
                    if(GameManager.getInstance().getCurrentGame().getLoadedMaps().isEmpty()) {
                        GameManager.getInstance().endGame(player.getLocation());
                    } else {
                        GameManager.getInstance().endGame(
                                GameManager.getInstance().getCurrentGame().getLoadedMaps().get(0)
                                        .getSpectatorLocation()
                                        .convert(GameManager.getInstance().getCurrentGame().getLoadedMaps().get(0).getInstance()));
                    }
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &7The match has been ended."));
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &7This command is currently not available."));
                }
                return;
            }
            if(args.length >= 2) {
                if(args[0].equalsIgnoreCase("set")) {
                    if(GameManager.getInstance().gameStatus.getID() > 1) {
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&bGame> &7This command is currently not available in games."));
                        return;
                    }
                    String locating = "";
                    for(int i=1; i<args.length; i++) {
                        if(!locating.isEmpty()) locating+=" ";
                        locating+=args[i];
                    }
                    boolean found = false;
                    boolean sendMessage = false;
                    for(Class<? extends Game> game : GameManager.getInstance().getGameListing()) {
                        try {
                            Game temp = game.newInstance();
                            if(temp.gameName().equalsIgnoreCase(locating)) {
                                if(GameManager.getInstance().getCurrentGame() != null) {
                                    if(GameManager.getInstance().getCurrentGame().gameName()
                                            .equalsIgnoreCase(temp.gameName())) {
                                        sendMessage = true;
                                    }
                                }
                                found = true;
                                GameManager.getInstance().setCurrentGame(temp);
                            }
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!found) {
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&bGame> &7The specified game could not be located."));
                    } else {
                        if(sendMessage) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bGame> &7The game has been set to &e"
                                    + GameManager.getInstance().getCurrentGame().gameName() + "&7."));
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                        }
                    }
                }
            }
        }
    }
}