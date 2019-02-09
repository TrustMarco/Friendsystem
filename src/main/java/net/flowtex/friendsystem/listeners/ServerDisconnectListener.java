package net.flowtex.friendsystem.listeners;

import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.commands.FriendCommand;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 03.01.2019
 * Zeit: 10:11
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

public class ServerDisconnectListener implements Listener {

    @EventHandler
    public void onServer(PlayerDisconnectEvent e) {
        ProxiedPlayer player = e.getPlayer();

            FriendManager friendmanager = new FriendManager(player.getUniqueId());
            friendmanager.setLast();
            for (UUID friend : friendmanager.getFriends()) {
                if (Main.getInstance().isPlayerOnline(friend)) {

                   Main.getInstance().sendMessage(friend, FriendCommand.prefix + "Dein Freund §e" + player.getName() + " §7ist nun §coffline§7.");
                }
            }
            friendmanager.sendStateToBukkit();
    }

}
