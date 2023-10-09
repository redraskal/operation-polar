package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.module.defaults.DoubleJumpModule;
import com.frostedmc.core.module.defaults.ForceGamemodeModule;
import com.frostedmc.core.module.defaults.NoDamageModule;
import com.frostedmc.core.module.defaults.NoHungerModule;
import com.frostedmc.core.utils.ColorCode;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class ModuleCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.DEV;
    }

    @Override
    public String commandLabel() {
        return "modules";
    }

    @Override
    public String commandDescription() {
        return "Manages the modules accessible on the server.";
    }

    private List<Module> disabledModules = new ArrayList<Module>();

    @Override
    public void onCommand(Player player, String[] args) {
        boolean cancel = false;

        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("disable")) {
                if(args.length > 1) {
                    Module search = null;

                    for(Module module : Core.getInstance().getModules()) {
                        if(module.name().equalsIgnoreCase(args[1].toLowerCase())) {
                            search = module;
                        }
                    }

                    if(search != null) {
                        cancel = true;
                        Core.getInstance().disableModule(search);
                        this.disabledModules.add(search);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_DISABLED.registerPlaceholder("%module%", search.name()).build());
                    }
                }
            }

            if(args[0].equalsIgnoreCase("enable")) {
                if(args.length > 1) {
                    Module search = null;

                    for(Module module : this.disabledModules) {
                        if(module.name().equalsIgnoreCase(args[1].toLowerCase())) {
                            search = module;
                        }
                    }

                    if(search != null) {
                        cancel = true;
                        Core.getInstance().enableModule(search);
                        this.disabledModules.remove(search);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_ENABLED.registerPlaceholder("%module%", search.name()).build());
                    }
                }
            }

            if(args[0].equalsIgnoreCase("options")) {
                if(args.length > 1) {
                    Module search = null;

                    for(Module module : Core.getInstance().getModules()) {
                        if(module.name().equalsIgnoreCase(args[1].toLowerCase())) {
                            search = module;
                        }
                    }

                    if(search != null) {
                        if(args.length > 3) {
                            boolean allow = false;

                            if(args[1].equalsIgnoreCase("doublejump")) {
                                DoubleJumpModule convertedModule = (DoubleJumpModule) search;

                                if(args[2].equalsIgnoreCase("thrust")) {
                                    try {
                                        Integer value = Integer.parseInt(args[3]);

                                        if(value > 200) {
                                            String optionFormatted = args[2];
                                            optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                            player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                    .registerPlaceholder("%value%", args[3])
                                                    .registerPlaceholder("%reason%", "Thrust too high, >200").build());
                                        } else {
                                            convertedModule.thrust = value;
                                            allow = true;
                                        }
                                    } catch (Exception e) {
                                        String optionFormatted = args[2];
                                        optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                .registerPlaceholder("%value%", args[3])
                                                .registerPlaceholder("%reason%", "Invalid integer").build());
                                    }

                                    cancel = true;
                                }

                                if(args[2].equalsIgnoreCase("sound")) {
                                    try {
                                        Sound value = Sound.valueOf(args[3].toUpperCase());
                                        convertedModule.jumpSound = value;
                                        allow = true;
                                    } catch (Exception e) {
                                        String optionFormatted = args[2];
                                        optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                .registerPlaceholder("%value%", args[3])
                                                .registerPlaceholder("%reason%", "Invalid sound").build());
                                    }

                                    cancel = true;
                                }
                            }

                            if(args[1].equalsIgnoreCase("nohunger")) {
                                NoHungerModule convertedModule = (NoHungerModule) search;

                                if(args[2].equalsIgnoreCase("foodlevel")) {
                                    try {
                                        Integer value = Integer.parseInt(args[3]);

                                        if(value > 20) {
                                            String optionFormatted = args[2];
                                            optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                            player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                    .registerPlaceholder("%value%", args[3])
                                                    .registerPlaceholder("%reason%", "Food level too high, >20").build());
                                        } else {
                                            convertedModule.foodLevel = value;
                                            allow = true;
                                        }
                                    } catch (Exception e) {
                                        String optionFormatted = args[2];
                                        optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                .registerPlaceholder("%value%", args[3])
                                                .registerPlaceholder("%reason%", "Invalid integer").build());
                                    }

                                    cancel = true;
                                }
                            }

                            if(args[1].equalsIgnoreCase("forcegamemode")) {
                                ForceGamemodeModule convertedModule = (ForceGamemodeModule) search;

                                if(args[2].equalsIgnoreCase("gamemode")) {
                                    try {
                                        GameMode value = GameMode.valueOf(args[3].toUpperCase());

                                        convertedModule.gameMode = value;
                                        allow = true;
                                    } catch (Exception e) {
                                        String optionFormatted = args[2];
                                        optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                .registerPlaceholder("%value%", args[3])
                                                .registerPlaceholder("%reason%", "Invalid GameMode").build());
                                    }

                                    cancel = true;
                                }
                            }

                            if(args[1].equalsIgnoreCase("nodamage")) {
                                NoDamageModule convertedModule = (NoDamageModule) search;

                                if(args[2].equalsIgnoreCase("hearts")) {
                                    try {
                                        Integer value = Integer.parseInt(args[3]);

                                        if(value > 20) {
                                            String optionFormatted = args[2];
                                            optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                            player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                    .registerPlaceholder("%value%", args[3])
                                                    .registerPlaceholder("%reason%", "Health level too high, >20").build());
                                        } else {
                                            convertedModule.hearts = value;
                                            allow = true;
                                        }
                                    } catch (Exception e) {
                                        String optionFormatted = args[2];
                                        optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                        player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_ERROR.registerPlaceholder("%option%", optionFormatted)
                                                .registerPlaceholder("%value%", args[3])
                                                .registerPlaceholder("%reason%", "Invalid integer").build());
                                    }

                                    cancel = true;
                                }
                            }

                            if(allow) {
                                cancel = true;
                                Core.getInstance().disableModule(search);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);

                                String optionFormatted = args[2];
                                optionFormatted = Character.toUpperCase(optionFormatted.charAt(0)) + optionFormatted.substring(1, optionFormatted.length());

                                player.sendMessage(PredefinedMessage.MODULE_COMMAND_OPTION_SET.registerPlaceholder("%option%", optionFormatted)
                                        .registerPlaceholder("%value%", args[3]).build());
                                Core.getInstance().enableModule(search);
                            }
                        }
                    }
                }
            }
        }

        if(!cancel) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
            player.sendMessage(PredefinedMessage.MODULE_COMMAND_LIST.build());

            for(Module module : Core.getInstance().getModules()) {
                new FancyMessage("  ")
                        .then(module.name() + " ")
                        .color(ChatColor.AQUA)
                        .then("- ")
                        .color(ChatColor.GRAY)
                        .then("DISABLE")
                        .color(ChatColor.RED)
                        .style(ChatColor.BOLD)
                        .tooltip("Click to disable module.")
                        .command("/modules disable " + module.name().toLowerCase())
                        .send(player);
            }

            if(Core.getInstance().getModules().isEmpty()) {
                player.sendMessage(ColorCode.RED.convert() + "  Not available.");
            }

            if(!this.disabledModules.isEmpty()) {
                player.sendMessage(ColorCode.GRAY.convert());
                player.sendMessage(PredefinedMessage.MODULE_COMMAND_LIST_DISABLED.build());

                for(Module module : this.disabledModules) {
                    new FancyMessage("  ")
                            .then(module.name() + " ")
                            .color(ChatColor.AQUA)
                            .then("- ")
                            .color(ChatColor.GRAY)
                            .then("ENABLE")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .tooltip("Click to enable module.")
                            .command("/modules enable " + module.name().toLowerCase())
                            .send(player);
                }
            }
        }
    }
}