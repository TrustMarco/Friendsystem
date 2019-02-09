package net.flowtex.friendsystem.commands;

import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 05.01.2019
 * Zeit: 15:39
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

public class RedoCommand extends Command {

    public RedoCommand() {
        super("r", null );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Main.getInstance().getPrefix() + "§cDies ist hier nicht möglich.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        FriendManager friendManager = new FriendManager(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(Main.getInstance().getPrefix() + "Nutze /r <Nachricht>");
        } else if (args.length >= 1) {
            if (Main.getInstance().redo.containsKey(player.getName())) {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(Main.getInstance().redo.get(player.getName()));
                if (Main.getInstance().isPlayerOnline(uuid)) {
                    String msg = "";
                    for (int i =0; i < args.length; i++) {
                        msg = msg + args[i] + " ";
                    }
                        if (friendManager.getFriends().contains(uuid)) {
                            if (friendManager.can("Message")) {
                                Main.getInstance().sendMessage(uuid, "§8[§bMSG§8] §e" + player.getName() + "§8 ➡ §7" + Main.getInstance().redo.get(player.getName()) + " §8» §7" + msg);
                                player.sendMessage("§8[§bMSG§8] §7" + player.getName() + "§8 ➡ §e" + Main.getInstance().redo.get(player.getName()) + " §8» §7" + msg);
                            } else {
                                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler hat das Empfangen von Nachrichten deaktiviert.");
                                Main.getInstance().redo.remove(player.getName());
                            }
                    } else {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler ist nicht mehr dein Freund.");
                            Main.getInstance().redo.remove(player.getName());
                    }
                } else {
                    player.sendMessage(Main.getInstance().getPrefix()+  "§cDein Chatpartner ist bereits Offline gegangen.");
                    Main.getInstance().redo.remove(player.getName());
                }
            } else {
                player.sendMessage(Main.getInstance().getPrefix()+  "§cDu hast derzeit keinen Chatpartner.");
            }
        }

    }
}
