package net.flowtex.friendsystem.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.commands.FriendCommand;
import net.flowtex.friendsystem.commands.PartyCommand;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 03.01.2019
 * Zeit: 11:15
 * <p>
 * Copyright
 * Durch die Schaffung eines Werkes entsteht nach deutschem Recht
 * automatisch ein Urheberrecht. Ein Copyright-Vermerk ist nicht erforderlich
 * Wie ein Werk urheberrechtlich geschützt ist, ist durch das Gesetzt
 * definiert. Die Person, die das Urheberrecht verletzt, kann für dieses
 * Vergehen abgemahnt werden. Hierbei werden für gewöhnlich die Abmahngebühr
 * in mindestens dreistelliger Höhe und Anwaltkosten fällig, sowie die Unterzeichnung
 * einer Unterlassungserklärung.
 * Auch der entstandene wirtschaftliche Schaden kann eingeklagt werden.
 * <p>
 * ---------------------------------------------------
 */

public class MessageListener implements Listener {



    @EventHandler
    public void onPupSub(PubSubMessageEvent e) {
        if (e.getChannel().equalsIgnoreCase("MESSAGE")) {
            String msg = e.getMessage().split(";")[1];
            UUID uuid = UUID.fromString(e.getMessage().split(";")[0]);

            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                if (msg.equalsIgnoreCase("PARTYACCEPT")) {
                    TextComponent accept = new TextComponent(PartyCommand.prefix + "§8«§aANNEHMEN§8» ");
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + e.getMessage().split(";")[2]));
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJa, ich möchte!").create()));

                    TextComponent deny = new TextComponent("§8«§cABLEHNEN§8»");
                    deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + e.getMessage().split(";")[2]));
                    deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cNein, ich möchte nicht!").create()));
                    accept.addExtra(deny);
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(accept);
                    return;
                } else if (msg.equalsIgnoreCase("FRIENDACCEPT")) {
                    TextComponent accept = new TextComponent(FriendCommand.prefix + "§8«§aANNEHMEN§8» ");
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + e.getMessage().split(";")[2]));
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJa, ich möchte!").create()));

                    TextComponent deny = new TextComponent("§8«§cABLEHNEN§8»");
                    deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + e.getMessage().split(";")[2]));
                    deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cNein, ich möchte nicht!").create()));

                    accept.addExtra(deny);
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(accept);
                    return;
                } else if (msg.equalsIgnoreCase("CONNECT")) {
                    String server = e.getMessage().split(";")[2];
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                    ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
                    return;
                } else {
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(msg);
                }
            }
        } else if (e.getChannel().equalsIgnoreCase("SPIGOT_MESSAGE")) {

            String msg = e.getMessage().split(";")[1];
            UUID uuid = UUID.fromString(e.getMessage().split(";")[0]);
            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(msg);
            ProxyServer.getInstance().getPlayer(uuid).getServer().sendData("BungeeCord", out.toByteArray());
            }
        }
    }

    @EventHandler
    public void onMessage(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = in.readUTF(); // channel we delivered
                ProxiedPlayer player = (ProxiedPlayer) e.getReceiver();
                FriendManager friendManager = new FriendManager(player.getUniqueId());
                if (channel.startsWith("ADDFRIEND=")) {
                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    String name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "friend accept " + name);
                } else if (channel.startsWith("STOP=")) {

                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    Main.getInstance().can_send.put(uuid, true);

                } else if (channel.startsWith("JUMP=")) {
                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    String name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "friend jump " + name);
                } else if (channel.startsWith("REMOVEFRIEND=")) {
                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    String name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "friend remove " + name);
                } else if (channel.startsWith("GETSERVER")) {
                    if (!friendManager.getFriends().isEmpty()) {
                        String list = "LIST=";
                        for (UUID friend : friendManager.getFriends()) {
                            if (Main.getInstance().isPlayerOnline(friend)) {
                                list = list + friend.toString() + "/" + RedisBungee.getApi().getServerFor(friend).getName() + ";";
                            }
                        }
                        Main.getInstance().sendMessageToSpigot(player.getUniqueId(), list);
                    }
                } else if (channel.startsWith("INVITEPARTY=")) {
                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "party invite " + Main.getInstance().getPlayerDataBaseManager().getName(uuid));
                 } else if (channel.startsWith("DENYFRIEND=")) {
                    UUID uuid = UUID.fromString(channel.split("=")[1]);
                    String name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "friend deny " + name);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
        }
    }

}
