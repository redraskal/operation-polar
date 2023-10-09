package com.frostedmc.core.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.defaults.*;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class CommandManager implements Listener, TabCompleter {

    private JavaPlugin javaPlugin;
    private List<Command> enabledCommands = new ArrayList<Command>();

    private static CommandManager instance;

    public static CommandManager getInstance() {
        return instance;
    }

    public JavaPlugin getPlugin() {
        return this.javaPlugin;
    }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }

        instance = new CommandManager(javaPlugin);
        return true;
    }

    private CommandManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;

        Core.getInstance().getLogger().info("[Manager] Enabling Command Manager...");
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        this.registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        this.registerCommand(new OpCommand());
        //this.registerCommand(new ModuleCommand());
        this.registerCommand(new CanICommand());
        this.registerCommand(new PingCommand());
        this.registerCommand(new HelpCommand());
        //this.registerCommand(new BlizzardCommand());
        //this.registerCommand(new CatsCommand());
        this.registerCommand(new BugReportCommand());
        this.registerCommand(new PunishCommand(javaPlugin));
        this.registerCommand(new WebsiteCommand());
        this.registerCommand(new DiscordCommand());
        this.registerCommand(new ClearChatCommand());
        this.registerCommand(new OptionsCommand());
        this.registerCommand(new ReportCommand());
        this.registerCommand(new ViewReportCommand());
        this.registerCommand(new CloseReportCommand());
        this.registerCommand(new ViewReportsCommand());
        this.registerCommand(new CrashCommand());
    }

    public void registerCommand(Command command) {
        Core.getInstance().getLogger().info("[Manager] Enabling /" + command.commandLabel() + "...");
        this.enabledCommands.add(command);
    }

    public void disableCommands() {
        for(Command command : this.enabledCommands) {
            Core.getInstance().getLogger().info("[Manager] Disabling /" + command.commandLabel() + "...");
        }

        this.enabledCommands.clear();
    }

    public List<Command> getEnabledCommands() {
        return this.enabledCommands;
    }

    @EventHandler
    public void onPreProcessCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        String[] array = playerCommandPreprocessEvent.getMessage().replace("/", "").split(" ");
        String commandLabel = array[0];
        String[] arguments = new String[]{};
        boolean found = false;

        if(commandLabel.contains(":") || commandLabel.contains("bukkit") || commandLabel.contains("minecraft")
                ||commandLabel.equalsIgnoreCase("pl")
                || commandLabel.equalsIgnoreCase("plugins")
                || commandLabel.equalsIgnoreCase("about") || commandLabel.equalsIgnoreCase("version")
                || commandLabel.equalsIgnoreCase("ver")
                || commandLabel.equalsIgnoreCase("") || commandLabel.equalsIgnoreCase("?")
                || commandLabel.equalsIgnoreCase("me")) {
            playerCommandPreprocessEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cThis command is currently disabled."));
            playerCommandPreprocessEvent.setCancelled(true);
            return;
        }

        if(array.length > 1) {
            List<String> argumentCache = new ArrayList<String>();
            boolean first = true;

            for(String argument : array) {
                if(first) {
                    first = false;
                } else {
                    argumentCache.add(argument);
                }
            }

            arguments = argumentCache.toArray(new String[argumentCache.size()]);
        }

        if(commandLabel.equalsIgnoreCase("demo")) {
            if(arguments.length > 0) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(playerCommandPreprocessEvent.getPlayer().getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.ADMIN)) {
                    if(Bukkit.getPlayer(arguments[0]) != null) {
                        Player enviar = Bukkit.getPlayer(arguments[0]);
                        String path = Bukkit.getServer().getClass().getPackage().getName();
                        String version = path.substring(path.lastIndexOf(".") + 1, path.length());

                        try {
                            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
                            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
                            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(new Class[] { Integer.TYPE, Float.TYPE });
                            Object packet = playOutConstructor.newInstance(new Object[] { Integer.valueOf(5), Integer.valueOf(0) });
                            Object craftPlayerObject = craftPlayer.cast(enviar);
                            Method getHandleMethod = craftPlayer.getMethod("getHandle", new Class[0]);
                            Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
                            Object pc = handle.getClass().getField("playerConnection").get(handle);
                            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", new Class[] { Packet });
                            sendPacketMethod.invoke(pc, new Object[] { packet });
                        } catch (Exception e) {}
                    }
                }
            }
            playerCommandPreprocessEvent.setCancelled(true);
            return;
        }

        if(commandLabel.equalsIgnoreCase("credits")) {
            if(arguments.length > 0) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(playerCommandPreprocessEvent.getPlayer().getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.ADMIN)) {
                    if(Bukkit.getPlayer(arguments[0]) != null) {
                        Player enviar = Bukkit.getPlayer(arguments[0]);
                        String path = Bukkit.getServer().getClass().getPackage().getName();
                        String version = path.substring(path.lastIndexOf(".") + 1, path.length());

                        try {
                            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
                            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
                            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(new Class[] { Integer.TYPE, Float.TYPE });
                            Object packet = playOutConstructor.newInstance(new Object[] { Integer.valueOf(4), Integer.valueOf(1) });
                            Object craftPlayerObject = craftPlayer.cast(enviar);
                            Method getHandleMethod = craftPlayer.getMethod("getHandle", new Class[0]);
                            Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
                            Object pc = handle.getClass().getField("playerConnection").get(handle);
                            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", new Class[] { Packet });
                            sendPacketMethod.invoke(pc, new Object[] { packet });
                        } catch (Exception e) {}
                    }
                }
            }
            playerCommandPreprocessEvent.setCancelled(true);
            return;
        }

        for(Command command : this.enabledCommands) {
            if(command.commandLabel().equalsIgnoreCase(commandLabel)) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(playerCommandPreprocessEvent.getPlayer().getUniqueId()).getRank();

                if(Rank.compare(rank, command.requiredRank())) {
                    command.onCommand(playerCommandPreprocessEvent.getPlayer(), arguments);
                } else {
                    playerCommandPreprocessEvent.getPlayer().sendMessage(PredefinedMessage.RANK_NEEDED.registerPlaceholder("%rank%", command.requiredRank().getPrefix(false)).build());
                }

                found = true;
            }
        }

        if(found) {
            playerCommandPreprocessEvent.setCancelled(true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return Arrays.asList(new String[]{});
        if(s.contains(":") || s.contains("bukkit") || s.contains("minecraft")
                || s.equalsIgnoreCase("pl")
                || s.equalsIgnoreCase("help") || s.equalsIgnoreCase("plugins")
                || s.equalsIgnoreCase("about") || s.equalsIgnoreCase("version")
                || s.equalsIgnoreCase("ver")
                || s.equalsIgnoreCase("") || s.equalsIgnoreCase("?")
                || s.equalsIgnoreCase("me")) {
            return Arrays.asList(new String[]{"We", "use", "custom", "plugins", "m8!"});
        }
        for(Command temp : this.getEnabledCommands()) {
            if(temp.commandLabel().equalsIgnoreCase(s)) {
                return Arrays.asList(temp.onTabComplete((Player) commandSender, strings));
            }
        }
        return Arrays.asList(new String[]{});
    }
}