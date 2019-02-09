package net.flowtex.friendsystem.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.flowtex.friendsystem.partymanager.PartyManager;
import net.flowtex.friendsystem.partymanager.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 04.01.2019
 * Zeit: 19:33
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

public class PartyCommand extends Command implements TabExecutor {

    public PartyCommand() {
        super ("party", null, "partie", "p");
    }

    public static String prefix = "§5§lParty §8× §7";

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(this.prefix + "§cDies kannst du hier nicht ausführen!");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        PlayerManager playerManager = new PlayerManager(player.getName());

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                if (playerManager.isInParty()) {
                    String party = playerManager.getParty(player);
                    PartyManager partyManager = new PartyManager(party);
                    if (partyManager.getOwner().equalsIgnoreCase(player.getName())) {
                        for (String name : partyManager.getMembers()) {
                            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                           Main.getInstance().sendMessage(uuid, this.prefix + "§e" + player.getName() + "§7 hat die Party aufgelöst.");
                        }
                        partyManager.delete();
                        player.sendMessage(this.prefix + "Die Party wurde erfolgreich aufgelöst.");
                    } else {
                        partyManager.removeMember(player.getName());
                        for (String name : partyManager.getMembers()) {
                            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                            Main.getInstance().sendMessage(uuid, this.prefix + "§e" + player.getName() + "§7 hat die Party §cverlassen§7.");
                        }
                        ProxyServer.getInstance().getPlayer(partyManager.getOwner()).sendMessage(this.prefix + "§e" + player.getName() + "§7 hat die Party §cverlassen§7.");
                        player.sendMessage(this.prefix + "Du hast die Party verlassen.");
                    }
                    Main.getInstance().getVars().party.remove(player);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (playerManager.isInParty()) {
                    String party = playerManager.getParty(player);
                    PartyManager partyManager = new PartyManager(party);
                    player.sendMessage("§8×§7--------§8× §dParty System §8×§7--------§8×");
                    player.sendMessage("§7Owner§8: §b" + partyManager.getOwner());
                    player.sendMessage("");
                    player.sendMessage("§7Mitglieder§8:");
                    player.sendMessage("");
                    for (String name : partyManager.getMembers()) {
                        player.sendMessage("§8» §e" + name);
                    }
                    player.sendMessage("");
                    player.sendMessage("§8×§7--------§8× §dParty System §8×§7--------§8×");
                }
            } else {
                this.sendHelp(player);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invite")) {
                String name = args[1];
                if (!player.getName().equalsIgnoreCase(name)) {
                    UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                    name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                    if (Main.getInstance().isPlayerOnline(uuid)) {
                        PlayerManager playerManager_target = new PlayerManager(name);
                        if (!(playerManager_target.getInvites().contains(player.getName()))) {
                            if (!new PlayerManager(player.getName()).getInvites().contains(name)) {
                                if (new FriendManager(uuid).can("Partyrequests")) {
                                    if (!new PartyManager(player.getName()).exists()) {
                                        new PartyManager(player.getName()).create(player.getName());
                                        player.sendMessage(this.prefix + "§aDu hast eine neue Party erstellt.");
                                    }
                                    Main.getInstance().getVars().party.put(player.getName(), player.getName());
                                    playerManager_target.addInvite(player.getName());
                                    Main.getInstance().sendMessage(uuid, this.prefix + "§e" + player.getName() + "§7 lädt dich in seine Party ein.");
                                    RedisBungee.getApi().sendChannelMessage("MESSAGE", uuid.toString() + ";" + "PARTYACCEPT;" + player.getName());


                                    player.sendMessage(this.prefix + "§aDu hast dem Spieler §7" + name + "§a eine Partyanfrage gesendet.");
                                } else {
                                    player.sendMessage(this.prefix + "§cDieser Spieler nimmt keine Partyanfragen an.");
                                }
                            } else {
                                player.sendMessage(this.prefix + "§cDieser Spieler hat bereits Dir eine Anfrage gesendet.");
                            }
                        } else {
                            player.sendMessage(this.prefix + "§cDu hast diesem Spieler bereits eine Einladung geschickt!");
                        }
                    } else {
                        player.sendMessage(this.prefix + "§cDieser Spieler ist nicht Online!");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDu kannst dir selbst keine Anfrage senden.");
                }
            } else if (args[0].equalsIgnoreCase("accept")) {
                String party = args[1];
                PartyManager partyManager = new PartyManager(party);

                if (partyManager.exists()) {
                    if (playerManager.getInvites().contains(party)) {
                        player.sendMessage(this.prefix + "§aDu bist erfolgreich der Party von §7" + party + "§a beigetreten.");
                        partyManager.addMember(player.getName());
                        playerManager.removeInvite(party);
                        Main.getInstance().getVars().party.put(player.getName(), party);
                        ProxyServer.getInstance().getPlayer(partyManager.getOwner()).sendMessage(this.prefix + "Der Spieler §e" + player.getName() + "§7 ist der Party beigeterten.");
                        for (String name : partyManager.getMembers()) {
                            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                            Main.getInstance().sendMessage(uuid, this.prefix + "Der Spieler §e" + player.getName() + "§7 ist der Party beigeterten.");
                        }
                    } else {
                        player.sendMessage(this.prefix + "§cDiese Party hat dir keine Anfrage gesendet.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDiese Party existiert nicht.");
                }
            } else if (args[0].equalsIgnoreCase("deny")) {
                String party = args[1];
                PartyManager partyManager = new PartyManager(party);

                if (partyManager.exists()) {
                    if (playerManager.getInvites().contains(party)) {
                        player.sendMessage(this.prefix + "Du hast die Partyanfrage von §e" + partyManager.getOwner() + "§7 abgelehnt.");
                        playerManager.removeInvite(party);
                        ProxyServer.getInstance().getPlayer(partyManager.getOwner()).sendMessage(this.prefix + "Der Spieler §e" + player.getName() + "§7 hat die Partyanfrage §cabgelehnt§7.");
                    } else {
                        player.sendMessage(this.prefix + "§cDiese Party hat dir keine Anfrage gesendet.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDiese Party existiert nicht.");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];
                if (!player.getName().equalsIgnoreCase(name)) {
                    if (playerManager.isInParty()) {
                        PartyManager partyManager = new PartyManager(playerManager.getParty(player));
                        if (partyManager.getOwner().equalsIgnoreCase(player.getName())) {
                            if (Main.getInstance().isPlayerOnline(name)) {
                                 UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                                 name = Main.getInstance().getPlayerDataBaseManager().getName(uuid);
                                if (partyManager.getMembers().contains(name)) {
                                    partyManager.removeMember(name);
                                    player.sendMessage(this.prefix + "Der Spieler §e" + name + "§7 wurde aus der Party entfernt.");
                                    for (String names : partyManager.getMembers()) {
                                        UUID uuid1 = Main.getInstance().getPlayerDataBaseManager().getUUID(names);
                                        Main.getInstance().sendMessage(uuid1, this.prefix + "Der Spieler §e" + name + "§7 wurde aus der Party entfernt.");
                                    }
                                    Main.getInstance().sendMessage(uuid, this.prefix + "Du wurdest aus der Party entfernt.");
                                    Main.getInstance().getVars().party.remove(name);
                                } else {
                                    player.sendMessage(this.prefix + "§cDieser Spieler befindet sich nicht in deiner Party.");
                                }
                            } else {
                                player.sendMessage(this.prefix + "§cDieser Spieler ist bereits Offline.");
                            }
                        } else {
                            player.sendMessage(this.prefix + "§cDazu musst du Party Ersteller sein.");
                        }
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDu kannst dich selber nicht aus der Party entfernen.");
                }
            } else this.sendHelp(player);
        } else this.sendHelp(player);
    }

    private void sendHelp(final ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage("§8§m------§5 Party §8§m------");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§7/party invite <Spieler>");
        proxiedPlayer.sendMessage("§7/party leave");
        proxiedPlayer.sendMessage("§7/party accept <Party>");
        proxiedPlayer.sendMessage("§7/party list");
        proxiedPlayer.sendMessage("§7/party deny <Party>");
        proxiedPlayer.sendMessage("§7/party remove <Spieler>");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§8§m------§5 Party §8§m------");
    }
    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        // stats kitsg Burnico all


        if (strings.length == 1) {
            ArrayList<String> list = new ArrayList<>();

            ArrayList<String> commands = new ArrayList<>();
            commands.add("invite");
            commands.add("remove");
            commands.add("list");
            commands.add("leave");
            commands.add("accept");
            commands.add("deny");

            if (strings[0].equalsIgnoreCase("")) return commands;

            for (String command : commands) {
                if (command.startsWith(strings[0])) {
                    list.add(command);
                }
            }

            return list;
        } else if (strings.length == 2) {
            ArrayList<String> list = new ArrayList<>();

            if (strings[1].equalsIgnoreCase("")) {
                return Main.getInstance().getPlayersOnline();
            }

            for (String name : Main.getInstance().getPlayersOnline()) {
                if (name.startsWith(strings[1])) {
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
