package net.flowtex.friendsystem.commands;

import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 05.01.2019
 * Zeit: 15:26
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

public class MessageCommand extends Command implements TabExecutor {

    public MessageCommand() {
        super ("message", null, "msg", "nachricht");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Main.getInstance().getPrefix() + "§cDies ist hier nicht möglich.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        FriendManager friendManager = new FriendManager(player.getUniqueId());

        if (args.length >= 2) {
            String name = args[0];
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg = msg + args[i]+ " ";
            }
            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
            name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
            if (Main.getInstance().isPlayerOnline(uuid)) {
                if (friendManager.getFriends().contains(uuid)) {
                    if (friendManager.can("Message")) {
                        Main.getInstance().sendMessage(uuid, "§8[§bMSG§8] §e" + player.getName() + "§8 ➡ §7" + name + " §8» §7" + msg);
                        player.sendMessage("§8[§bMSG§8] §7" + player.getName() + "§8 ➡ §e" + name + " §8» §7" + msg);
                        Main.getInstance().redo.put(player.getName(), name);
                        Main.getInstance().redo.put(name, player.getName());
                    } else {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler hat das Empfangen von Nachrichten deaktiviert.");
                    }
                } else {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler ist nicht dein Freund.");
                }
            } else {
                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler ist nicht Online.");
            }
        } else {
            player.sendMessage(Main.getInstance().getPrefix() + "Nutze /msg <Spieler> <Nachricht>");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        // stats kitsg Burnico all

         if (strings.length == 1) {
            ArrayList<String> list = new ArrayList<>();

             if (strings[0].equalsIgnoreCase("")) {
                 return Main.getInstance().getPlayersOnline();
             }

            for (String name : Main.getInstance().getPlayersOnline()) {
                if (name.startsWith(strings[0])) {
                    list.add(name);
                }
            }
            return list;
        }

// stats Burnico kitsg all
        ArrayList<String> noPerm = new ArrayList<>();
        return noPerm;
    }
}
