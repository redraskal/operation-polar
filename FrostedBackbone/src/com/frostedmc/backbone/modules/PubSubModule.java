package com.frostedmc.backbone.modules;

import com.frostedmc.backbone.Backbone;
import com.frostedmc.backbone.automate.ServerInfo;
import com.frostedmc.backbone.automate.ServerManager;
import com.frostedmc.backbone.automate.TemplateInfo;
import com.frostedmc.backbone.automate.TemplateManager;
import com.frostedmc.backbone.commands.MessageCommand;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.module.Module;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Redraskal_2 on 10/30/2016.
 */
public class PubSubModule extends Module implements Listener {

    private Backbone instance;
    private Map<String, ScheduledTask> tasks = new HashMap<String, ScheduledTask>();

    public PubSubModule(Backbone instance) {
        this.instance = instance;
    }

    @Override
    public String name() {
        return "Redis Pub-Sub";
    }

    @Override
    public void onEnable() {
        this.instance.getProxy().getPluginManager().registerListener(this.instance, this);
        RedisBungee.getApi().registerPubSubChannels("alert");
        RedisBungee.getApi().registerPubSubChannels("ne");
        RedisBungee.getApi().registerPubSubChannels("playpen-create");
        RedisBungee.getApi().registerPubSubChannels("staffchat");
        RedisBungee.getApi().registerPubSubChannels("report");
        RedisBungee.getApi().registerPubSubChannels("spam");
        RedisBungee.getApi().registerPubSubChannels("transactions-vote");
        RedisBungee.getApi().registerPubSubChannels("transactions-purchase");
        RedisBungee.getApi().registerPubSubChannels("custom-rca");
        RedisBungee.getApi().registerPubSubChannels("server-cat-status");
        RedisBungee.getApi().registerPubSubChannels("message-command");
        ProxyServer.getInstance().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
            public void run() {
                for(TemplateInfo template : TemplateManager.getInstance().getTemplates()) {
                    int count = 0;
                    for(ServerInfo serverInfo : ServerManager.getInstance().getServers(template)) {
                        if(ProxyServer.getInstance().getServerInfo(serverInfo.getServerName()) != null) {
                            count+=ProxyServer.getInstance().getServerInfo(serverInfo.getServerName())
                                    .getPlayers().size();
                        }
                    }
                    RedisBungee.getApi().sendChannelMessage("server-cat-status", template.getName() + ":" + count);
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        this.instance.getProxy().getPluginManager().unregisterListener(this);
        RedisBungee.getApi().unregisterPubSubChannels("alert");
        RedisBungee.getApi().unregisterPubSubChannels("ne");
        RedisBungee.getApi().unregisterPubSubChannels("playpen-create");
        RedisBungee.getApi().unregisterPubSubChannels("staffchat");
        RedisBungee.getApi().unregisterPubSubChannels("report");
        RedisBungee.getApi().unregisterPubSubChannels("spam");
        RedisBungee.getApi().unregisterPubSubChannels("transactions-vote");
        RedisBungee.getApi().unregisterPubSubChannels("transactions-purchase");
        RedisBungee.getApi().unregisterPubSubChannels("custom-rca");
        RedisBungee.getApi().unregisterPubSubChannels("server-cat-status");
        RedisBungee.getApi().unregisterPubSubChannels("message-command");
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if(event.getChannel().equalsIgnoreCase("message-command")) {
            String username = event.getMessage().split("\n")[0];
            String from = event.getMessage().split("\n")[1];
            String message = event.getMessage().split("\n")[2];
            if(ProxyServer.getInstance().getPlayer(username) != null) {
                ProxyServer.getInstance().getPlayer(username).sendMessage(ChatMessageType.CHAT, new TextComponent(
                        ChatColor.translateAlternateColorCodes('&',
                                "&b&l" + from + " &3-> &b&lYou &7" + message)
                ));
                MessageCommand.lastMessage.put(ProxyServer.getInstance().getPlayer(username).getUniqueId(),
                        Core.getInstance().getUUIDFetcher().parseDetails(from));
            }
        }
        if(event.getChannel().equalsIgnoreCase("custom-rca")) {
            Backbone.getInstance().getProxy().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
                public void run() {
                    if(ServerManager.getInstance().doesServerExist(event.getMessage())) {
                        if(ServerManager.getInstance().getServer(event.getMessage()).isStarted()) {
                            ServerManager.getInstance().stopServer(ServerManager.getInstance().getServer(event.getMessage()));
                        }
                        ServerManager.getInstance().deleteServer(ServerManager.getInstance().getServer(event.getMessage()));
                        ServerInfo serverInfo = ServerManager.getInstance()
                                .createServer(TemplateManager.getInstance().getTemplate(event.getMessage().split("-")[0]), event.getMessage());
                        ServerManager.getInstance().startServer(serverInfo);
                    }
                }
            }, 6, TimeUnit.SECONDS);
        }
        if(event.getChannel().equalsIgnoreCase("transactions-vote")) {
            ResultSet resultSet = Core.getInstance().getAccountManager().queryAccountDetails(event.getMessage());
            try {
                if(resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    int icicles = resultSet.getInt("icicles");
                    Core.getInstance().getAccountManager().update(uuid,
                            new UpdateDetails(UpdateDetails.UpdateType.ICICLES, (icicles+100)));
                }
            } catch (Exception e) {}
            if(ProxyServer.getInstance().getPlayer(event.getMessage()) != null) {
                ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', "&6Server> &7Thanks for voting! You have received 100 icicles.")
                ));
            }
        }
        if(event.getChannel().equalsIgnoreCase("transactions-purchase")) {
            //TODO
        }
        if(event.getChannel().equalsIgnoreCase("alert")) {
            this.instance.getProxy().broadcast(new TextComponent(event.getMessage()));
        }
        if(event.getChannel().equalsIgnoreCase("ne")) {
            this.instance.getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    "&c&m-----------------------------------------------------")));
            this.instance.getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    "&4&lNetwork Alert: &7" + ChatColor.stripColor(event.getMessage()))));
            this.instance.getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    "&c&m-----------------------------------------------------")));
        }
        if(event.getChannel().equalsIgnoreCase("staffchat")) {
            UUID uuid = UUID.fromString(event.getMessage().split("\n")[0]);
            String username = event.getMessage().split("\n")[1];
            String message = event.getMessage().split("\n")[2];
            String prefix = Core.getInstance().getAccountManager().parseDetails(uuid).getRank().getPrefix(false);
            for(ProxiedPlayer proxiedPlayer : BungeeCord.getInstance().getPlayers()) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(proxiedPlayer.getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.HELPER)) {
                    proxiedPlayer.sendMessage(new TextComponent(
                            ChatColor.translateAlternateColorCodes('&', "&3<< Staff Chat >> " + prefix + " &7" + username + " &f" + message)));
                }
            }
        }
        if(event.getChannel().equalsIgnoreCase("report")) {
            String username = event.getMessage().split("\n")[0];
            String from = event.getMessage().split("\n")[1];
            String reporting = event.getMessage().split("\n")[2];
            String reason = event.getMessage().split("\n")[3];
            for(ProxiedPlayer proxiedPlayer : BungeeCord.getInstance().getPlayers()) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(proxiedPlayer.getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.HELPER)) {
                    proxiedPlayer.sendMessage(new TextComponent(
                            ChatColor.translateAlternateColorCodes('&', "&c&l[" + from + "] &7" + username + " &9-> &7" + reporting + " &f" + reason)));
                }
            }
        }
        if(event.getChannel().equalsIgnoreCase("playpen-create")) {
            System.out.println(ChatColor.translateAlternateColorCodes('&',
                    "&b&m----------------------&r &3PLAYPEN &b&m----------------------"));
            System.out.println(ChatColor.translateAlternateColorCodes('&', event.getMessage().split("\n")[0]));
            System.out.println(ChatColor.translateAlternateColorCodes('&', event.getMessage().split("\n")[1]));
            System.out.println(ChatColor.translateAlternateColorCodes('&',
                    "&b&m-----------------------------------------------------"));
            for(ProxiedPlayer proxiedPlayer : BungeeCord.getInstance().getPlayers()) {
                Rank rank = Core.getInstance().getAccountManager().parseDetails(proxiedPlayer.getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.ADMIN)) {
                    proxiedPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                            "&b&m----------------------&r &3PLAYPEN &b&m----------------------")));
                    proxiedPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', event.getMessage().split("\n")[0])));
                    proxiedPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', event.getMessage().split("\n")[1])));
                    proxiedPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                            "&b&m-----------------------------------------------------")));
                }
            }
        }
        if(event.getChannel().equalsIgnoreCase("spam")) {
            if(ProxyServer.getInstance().getPlayer(event.getMessage()) != null) {
                tasks.put(event.getMessage(), ProxyServer.getInstance().getScheduler().schedule(Backbone.getInstance(), new Runnable() {
                    public void run() {
                        if (ProxyServer.getInstance().getPlayer(event.getMessage()) != null) {
                            switch(new Random().nextInt(9)) {
                                case 1:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.MAGIC
                                            + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                                    break;
                                case 2:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.MAGIC
                                            + ""
                                            + ChatColor.GOLD
                                            + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                                    break;
                                case 3:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.MAGIC
                                            + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                                    break;
                                case 4:
                                    ProxyServer.getInstance().getPlayer(event.getMessage())
                                            .sendMessage(" !!!!!!!!!!!!!! CLICK HERE TO CLAIM YOUR FREE EYE PAD! !!!!!!!!!!!!!!!! ");
                                    break;
                                case 5:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.AQUA
                                            + ">>>>>>>>>> LOOKING FOR SOMETHING? TRY ASK JEVES! <<<<<<<<<<<");
                                    break;
                                case 6:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.GOLD
                                            + "WIN OVER 50 THOUSAND DOLLARS JUST BY CALLING THIS NUMBER!");
                                    break;
                                case 7:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.UNDERLINE
                                            + "TAKE OUR SURVAY ON HOW YOU THINK FUTURE SURVAYS SHOULD SURVAY THE POPULATION'S OPINION ON SURVAYS!");
                                    break;
                                case 8:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.BLACK + "NYAN"
                                            + ChatColor.BLUE + "NYAN" + ChatColor.RED
                                            + "NYAN" + ChatColor.GREEN + "NYAN"
                                            + ChatColor.RED + "NYAN" + ChatColor.YELLOW
                                            + "NYAN" + ChatColor.RED + "NYAN");
                                    break;
                                case 9:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.UNDERLINE
                                            + "'Knock knock' 'Who's there?' 'The landlord. Your rent is due'");
                                    break;
                                case 10:
                                    ProxyServer.getInstance().getPlayer(event.getMessage()).sendMessage(ChatColor.ITALIC
                                            + "I'm afraid I can't let you do that " + ProxyServer.getInstance().getPlayer(event.getMessage()).getDisplayName());
                                    break;
                            }
                        } else {
                            tasks.get(event.getMessage()).cancel();
                        }
                    }
                }, 1, 1, TimeUnit.SECONDS));
            }
        }
    }
}
