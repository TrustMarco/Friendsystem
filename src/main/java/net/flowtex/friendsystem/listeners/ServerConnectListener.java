package net.flowtex.friendsystem.listeners;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.commands.FriendCommand;
import net.flowtex.friendsystem.commands.PartyCommand;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.flowtex.friendsystem.partymanager.PartyManager;
import net.flowtex.friendsystem.partymanager.PlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 02.01.2019
 * Zeit: 16:36
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

public class ServerConnectListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer player = e.getPlayer();
        FriendManager friendManager = new FriendManager(player.getUniqueId());
        if (player.getServer() == null) {
            if (!friendManager.getFriends().isEmpty()) {
                friendManager.sendServerToBukkit(e.getTarget().getName());
            }

            if (!(friendManager.getFriends().isEmpty())) {
                player.sendMessage(FriendCommand.prefix + "Derzeit sind §e" + friendManager.getOnlineFriendCount() + "§7 deiner Freunde online.");
                for (UUID uuid : friendManager.getFriends()) {
                    if (Main.getInstance().isPlayerOnline(uuid)) {
                      Main.getInstance().sendMessage(uuid,FriendCommand.prefix + "Dein Freund §e" + player.getName() + "§7 ist jetzt §aonline§7!");
                    }
                }
            }
            if (!(friendManager.getRequests().isEmpty())) {
                player.sendMessage(FriendCommand.prefix + "Du besitzt §e" + friendManager.getRequests().size() + "§7 offene Anfragen.");
            }
        } else {
            if (!friendManager.getFriends().isEmpty()) {
                friendManager.sendServerToBukkit(e.getTarget().getName());
            }
            PlayerManager playerManager = new PlayerManager(player.getName());
            if (Main.getInstance().getVars().party.containsKey(ProxyServer.getInstance().getPlayer(player.getName()))) {
                String party = playerManager.getParty(player);
                if (new PartyManager(party).getOwner().equalsIgnoreCase(player.getName())) {
                    if (!e.getTarget().getName().startsWith("lobbby")) {
                        for (String name : new PartyManager(party).getMembers()) {

                            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                            RedisBungee.getApi().sendChannelMessage("MESSAGE", uuid.toString() + ";CONNECT;" + e.getTarget().getName());
                            Main.getInstance().sendMessage(uuid, PartyCommand.prefix + "Die Party betritt §e" + e.getTarget().getName() + "§7.");
                        }
                        player.sendMessage(PartyCommand.prefix + "Die Party betritt §e" + e.getTarget().getName() + "§7.");
                    }      
                }
            }

        }
    }

}
