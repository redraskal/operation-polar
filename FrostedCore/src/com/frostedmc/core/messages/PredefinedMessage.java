package com.frostedmc.core.messages;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public enum PredefinedMessage {

    NOT_ENOUGH_ARGUMENTS(Prefix.COMMAND, "You didn't fill in all required arguments, use the command like this: %usage%."),
    CANNOT_EXECUTE(Prefix.COMMAND, "You cannot execute this command at the current time."),
    COMMAND_NOT_FOUND(Prefix.COMMAND, "Command not found!"),
    COMMAND_ERROR(Prefix.COMMAND, "&cOh no! &7It looks like something went wrong with the command execution. Please check your arguments."),

    LIST_OF_COMMANDS(Prefix.COMMANDS, "List of Commands:"),
    COMMAND_IN_LIST(Prefix.NONE, "&4/%name% &7&l- %rank%"),
    COMMAND_IN_LIST_MUTEABLE(Prefix.NONE, "&4/%name% &7&l- %rank% &6&l*"),
    COMMAND_IN_LIST_PLAYER(Prefix.NONE, "&4/%name%"),
    COMMAND_IN_LIST_MUTEABLE_PLAYER(Prefix.NONE, "&4/%name% &7&l- &6&l*"),
    COMMAND_LIST_MUTEABLE(Prefix.NONE, "&6&l* &6Muteable if spammed or abused"),
    COMMAND_IN_LIST_DESC(Prefix.NONE, "&e%desc%"),

    PLAYER_NOT_FOUND(Prefix.ACCOUNT, "&4%username% &7does not have an account on FrostedMC."),

    RANK_NEEDED(Prefix.RANK, "You need %rank%&7 to execute this action."),
    RANK_COMMAND_GET(Prefix.RANK, "&a%username%'s &7rank is %rank%&7."),
    RANK_COMMAND_SET(Prefix.RANK, "&a%username%'s &7rank has been set to %rank%&7."),
    INVALID_RANK(Prefix.RANK, "%rank% &7is an &cinvalid&7 rank."),

    MODULE_COMMAND_LIST(Prefix.MODULE, "&7Currently enabled modules:"),
    MODULE_COMMAND_LIST_DISABLED(Prefix.MODULE, "&7Currently disabled modules:"),
    MODULE_COMMAND_DISABLED(Prefix.MODULE, "&b%module% &7has been disabled."),
    MODULE_COMMAND_ENABLED(Prefix.MODULE, "&b%module% &7has been enabled."),
    MODULE_COMMAND_OPTION_SET(Prefix.MODULE, "&b%option% &7has been set to &a%value%&7."),
    MODULE_COMMAND_OPTION_ERROR(Prefix.MODULE, "&b%option% &7cannot be set to &a%value%&7 &8(&7%reason%&8)&7."),

    PING_COMMAND_PLAYER(Prefix.PING, "Your ping is &a%ping%ms"),
    PING_COMMAND_OTHERS(Prefix.PING, "&e%player%&7's ping is &a%ping%ms"),

    SERVER_SEND(Prefix.SERVER, "Sending you to &b%server%&7..."),

    COSMETICS_GADGET_ENABLE(Prefix.COSMETICS, "&7You have enabled the &b%gadget% Gadget&7."),
    COSMETICS_GADGET_DISABLE(Prefix.COSMETICS, "&7You have disabled the &b%gadget% Gadget&7."),
    COSMETICS_COOLDOWN(Prefix.COSMETICS, "&7You must wait &b%seconds% &7seconds before using this again."),

    COSMETICS_PARTICLE_ENABLE(Prefix.COSMETICS, "&7You have enabled the &b%particle% Particle&7."),
    COSMETICS_PARTICLE_DISABLE(Prefix.COSMETICS, "&7You have disabled the &b%particle% Particle&7."),

    BLIZZARD_ENABLED(Prefix.SERVER, "&7A blizzard is already starting!"),
    BLIZZARD_ENABLE(Prefix.SERVER, "&7A blizzard has been started."),

    CATS_ENABLED(Prefix.SERVER, "&7CATS ARE ALREADY COMING :O"),
    CATS_ENABLE(Prefix.SERVER, "&7Cat mode activate."),

    GLACIER_LOG_EXPORTED(Prefix.GLACIER, "&7Debug log has been exported."),

    FEATURE_COMING_SOON(Prefix.SERVER, "&7This feature is coming soon."),

    KINGDOMS_TEST_COMMAND_ERR(Prefix.KINGDOMS, "&7Please specify a schematic to paste!"),
    KINGDOMS_TEST_COMMAND(Prefix.KINGDOMS, "&7Pasting the schematic now..."),
    KINGDOMS_TEST_COMMAND_DONE(Prefix.KINGDOMS, "&7The schematic has been pasted!"),

    REPORT_BUILD_ALREADY(Prefix.REPORT, "&7You have already reported this build!"),
    REPORT_BUILD_SELF(Prefix.REPORT, "&7You cannot report your own build!"),
    REPORT_BUILD(Prefix.REPORT, "&7Thank you for reporting this build to help keep the game clean!"),

    VOTED(Prefix.GAME, "&7You have voted for %stars% &7stars."),
    QUERY_PROGRESS(Prefix.SERVER, "&7Searching for available skulls... (this may take a while)"),
    ERROR_IN_QUERY(Prefix.SERVER, "&7An error has occurred while obtaining data."),

    SKULL_RECIEVED(Prefix.SERVER, "&7You have been given the skull of &a%username%&7."),

    JOIN(Prefix.JOIN, "%username%"),
    LEAVE(Prefix.LEAVE, "%username%"),

    NOW_OPPED_COMMAND(Prefix.NONE, "&7&o[Server: Opped %username%]"),
    CAN_I_COMMAND(Prefix.SERVER, "&7No, you can't.");

    public Prefix p;
    public String m;

    public Map<String, String> pH;

    private PredefinedMessage(Prefix prefix, String message) {
        this.p = prefix;
        this.m = message;
        this.pH = new HashMap<String, String>();
    }

    public PredefinedMessage registerPlaceholder(String placeholder, String replaceWith) {
        pH.put(placeholder, replaceWith);
        return this;
    }

    public String getMessage(boolean usePlaceholders) {
        if(usePlaceholders) {
            String replaced = "" + m;

            for(Map.Entry<String, String> replace: pH.entrySet()) {
                replaced = replaced.replace(replace.getKey(), replace.getValue());
            }

            return replaced;
        } else {
            return m;
        }
    }

    public String build() {
        return p.build() + ChatColor.translateAlternateColorCodes('&', getMessage(true));
    }
}