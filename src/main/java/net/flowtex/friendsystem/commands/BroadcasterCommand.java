package net.flowtex.friendsystem.commands;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Krentox.net] Broadcaster
 * Author: Marco Weiß (Burnico)
 * Datum: 01.12.2018
 * Zeit: 20:29
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

public class BroadcasterCommand extends Command {

    public BroadcasterCommand() {
        super("broadcaster", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("bungeesystem.command.broadcaster")) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

            if (args.length == 0) {
                proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                proxiedPlayer.sendMessage("§7/broadcaster add <Name> <Nachricht>");
                proxiedPlayer.sendMessage("§7/broadcaster addtext <Name> <Nachricht>");
                proxiedPlayer.sendMessage("§7/broadcaster setTime [Sekunden]");
                proxiedPlayer.sendMessage("§7/broadcaster list");
                proxiedPlayer.sendMessage("§7/broadcaster edit <Name> <Nachricht>");
                proxiedPlayer.sendMessage("§7/broadcaster delete <Name>");
                proxiedPlayer.sendMessage("§7/broadcaster getTime");
                proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Alle Broadcast Nachrichten:");
                    proxiedPlayer.sendMessage("");
                    for (String msg : Main.getInstance().getBroadcastManager().getMessages()) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§e" +  msg+ "§8: " + Main.getInstance().getBroadcastManager().getMessage(msg).replace("&", "§"));
                    }
                } else if (args[0].equalsIgnoreCase("getTime")) {
                    proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Broadcast Zeit: §e" + Main.getInstance().getBroadcastManager().getTime() + " Sekunden");
                } else {
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                    proxiedPlayer.sendMessage("§7/broadcaster add <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster addtext <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster setTime [Sekunden]");
                    proxiedPlayer.sendMessage("§7/broadcaster list");
                    proxiedPlayer.sendMessage("§7/broadcaster edit <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster delete <Name>");
                    proxiedPlayer.sendMessage("§7/broadcaster getTime");
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];

                    if (Main.getInstance().getBroadcastManager().exist(name)) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Lösche Nachricht...");
                        Main.getInstance().getBroadcastManager().removeMessage(name);
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Die Nachricht §e" + name + "§7 wurde erfolgreich gelöscht.");
                    } else {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§cDiese Broadcast Nachricht existiert nicht.");
                    }
                } else if (args[0].equalsIgnoreCase("settime")) {
                    try {
                        int time = Integer.parseInt(args[1]);

                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Setze Zeit...");
                        Main.getInstance().getBroadcastManager().setTime(String.valueOf(time));
                        Main.getInstance().getBroadcastManager().startBroadcaster();
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Die Zeit wurde eroflgreich auf §e" + time + " Sekunden §7gesetzt.");
                    } catch (NumberFormatException e) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§cBitte wähle eine realistische Numerale aus.");
                    }
                } else {
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                    proxiedPlayer.sendMessage("§7/broadcaster add <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster addtext <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster setTime [Sekunden]");
                    proxiedPlayer.sendMessage("§7/broadcaster list");
                    proxiedPlayer.sendMessage("§7/broadcaster edit <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster delete <Name>");
                    proxiedPlayer.sendMessage("§7/broadcaster getTime");
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                }
            } else if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    String name = args[1];

                    String msg = "";

                    for (int i = 2; i < args.length; i++) {
                        msg = msg + args[i] + " ";
                    }

                    if (!(Main.getInstance().getBroadcastManager().exist(name))) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Erstelle Nachricht...");
                        Main.getInstance().getBroadcastManager().setMessage(name, msg);
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Die Nachricht §e" + name + " §7wurde erfolgreich erstellt.");
                    } else {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§cBitte wähle einen anderen Namen aus. Dieser existiert bereits.");
                    }
                } else if (args[0].equalsIgnoreCase("edit")) {
                    String name = args[1];

                    String msg = "";

                    for (int i = 2; i < args.length; i++) {
                        msg = msg + args[i] + " ";
                    }

                    if ((Main.getInstance().getBroadcastManager().exist(name))) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Editiere Nachricht...");
                        Main.getInstance().getBroadcastManager().setMessage(name, msg);
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Die Nachricht §e" + name + " §7wurde erfolgreich editiert.");
                    } else {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§cDiese Nachricht muss zuvor erstellt werden.");
                    }
                } else if (args[0].equalsIgnoreCase("addtext")) {
                    String name = args[1];

                    String msg = "";

                    for (int i = 2; i < args.length; i++) {
                        msg = msg + args[i] + " ";
                    }

                    if ((Main.getInstance().getBroadcastManager().exist(name))) {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Füge Nachricht hinzu...");
                        Main.getInstance().getBroadcastManager().setMessage(name, Main.getInstance().getBroadcastManager().getMessage(name) + msg);
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "Die Nachricht §e" + name + " §7wurde erfolgreich editiert.");
                    } else {
                        proxiedPlayer.sendMessage(Main.getInstance().getPrefix() + "§cDiese Nachricht muss zuvor erstellt werden.");
                    }
            } else {
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                    proxiedPlayer.sendMessage("§7/broadcaster add <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster addtext <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster setTime [Sekunden]");
                    proxiedPlayer.sendMessage("§7/broadcaster list");
                    proxiedPlayer.sendMessage("§7/broadcaster edit <Name> <Nachricht>");
                    proxiedPlayer.sendMessage("§7/broadcaster delete <Name>");
                    proxiedPlayer.sendMessage("§7/broadcaster getTime");
                    proxiedPlayer.sendMessage("§8§m------ §aBroadcaster §8§m------");
                }
            }
        } else {
            sender.sendMessage(Main.getInstance().getPrefix() + "§cDazu hast du keine Rechte!");
        }
    }
}
