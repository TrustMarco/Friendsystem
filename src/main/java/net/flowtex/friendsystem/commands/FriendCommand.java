package net.flowtex.friendsystem.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.flowtex.friendsystem.Main;
import net.flowtex.friendsystem.friendmanager.FriendManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 02.01.2019
 * Zeit: 15:30
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
public class FriendCommand extends Command implements TabExecutor {

    public FriendCommand() {
        super("Friend", null, "freunde", "f");
    }

    public static String prefix = "§4§lFreunde §8× §7";

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(this.prefix + "§cDies kannst du hier nicht!");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        FriendManager friendManager = new FriendManager(player.getUniqueId());

        if (args.length == 0) {
            this.sendFriendhelp(player, 1);
        } else if (args.length == 1) {
            if (args[0].equals("list")) {
                if ((friendManager.existsFriends())) {
                    player.sendMessage(this.prefix + "§6Liste deiner Freunde§8:");
                    player.sendMessage("");
                    for (UUID friend : friendManager.getFriends()) {
                        String name = Main.getInstance().getPlayerDataBaseManager().getName(friend);
                        player.sendMessage("§8» §e" + name + " §7[" + (Main.getInstance().isPlayerOnline(name) ? "§aOnline" : "§cOffline") + "§7]");
                    }
                    player.sendMessage("");
                } else {
                    player.sendMessage(this.prefix + "§cDu besitzt noch keine Freunde.");
                }
            } else if (args[0].equalsIgnoreCase("requests")) {

                if ((friendManager.existsRequests())) {
                    player.sendMessage(this.prefix + "§6Liste deiner Anfragen§8:");
                    player.sendMessage("");
                    for (UUID friend : friendManager.getRequests()) {
                        String name = Main.getInstance().getPlayerDataBaseManager().getName(friend);
                        player.sendMessage("§8» §7" + name);
                    }
                    player.sendMessage("");
                } else {
                    player.sendMessage(this.prefix + "§cDu besitzt keine Anfragen.");
                }
            } else if (args[0].equalsIgnoreCase("confirm")) {
                player.sendMessage(this.prefix + "Lösche Freundesliste...");
                for (UUID friend : friendManager.getFriends()) {
                    FriendManager friendManager1 = new FriendManager(friend);
                    friendManager1.removeFriend(friend);
                }
                friendManager.clear();
                player.sendMessage(this.prefix + "§aDeine Freundesliste wurde geleert.");
            } else if (args[0].equalsIgnoreCase("add")) {
                player.sendMessage(this.prefix + "§7Nutze /friend add <Spieler>");
            } else if (args[0].equalsIgnoreCase("clear")) {
                TextComponent accept = new TextComponent(this.prefix + "§8«§a§lBESTÄTIGEN§8» ");
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend confirm"));
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJa, ich möchte!").create()));
                player.sendMessage(this.prefix + "Willst du wirklich deine Freundesliste leeren?");
                player.sendMessage(accept);
            } else if (args[0].equalsIgnoreCase("acceptall")) {
                if (!(friendManager.getRequests().isEmpty())) {
                    int count = 0;
                    for (UUID uuid : friendManager.getRequests()) {
                        count++;
                        FriendManager friendTarget = new FriendManager(uuid);
                        friendManager.removeRequests(uuid);
                        friendTarget.addFriend(player.getUniqueId());
                        friendManager.addFriend(uuid);
                        player.sendMessage(this.prefix + "§aDie Freundesanfrage von §7" + Main.getInstance().getPlayerDataBaseManager().getName(uuid) + " §awurde erfolgreich akzeptiert.");
                        if (Main.getInstance().isPlayerOnline(uuid)) {
                            Main.getInstance().sendMessage(uuid, this.prefix + "Der Spieler §a" + player.getName() + "§7 hat deine Freundschaftsanfrage akzeptiert.");
                            Main.getInstance().sendMessageToSpigot(uuid, "RESET");
                        }
                    }

                    player.sendMessage("");
                    player.sendMessage(this.prefix + "Es wurden §e" + count + " Freundschaftsanfragen §7angenommen.");
                    Main.getInstance().sendMessageToSpigot(player.getUniqueId(), "RESET");
                } else {
                    player.sendMessage(this.prefix + "§cDu besitzt keine Freundesanfragen.");
                }
            } else if (args[0].equalsIgnoreCase("denyall")) {
                if (!(friendManager.getRequests().isEmpty())) {

                    int count = 0;
                    for (UUID uuid : friendManager.getRequests()) {
                        count++;
                        friendManager.removeRequests(uuid);
                        player.sendMessage(this.prefix + "Die Freundesanfrage von §e" + Main.getInstance().getPlayerDataBaseManager().getName(uuid) + " §7wurde erfolgreich §cabgelehnt§7.");
                        if (Main.getInstance().isPlayerOnline(uuid)) {
                           Main.getInstance().sendMessage(uuid, this.prefix + "Der Spieler §e" + player.getName() + "§7 hat deine Freundschaftsanfrage §cabgelehnt§7.");
                        }
                    }

                    player.sendMessage("");
                    player.sendMessage(this.prefix + "Es wurden §e" + count + " Freundschaftsanfragen §cabgelehnt§7.");

                } else {
                    player.sendMessage(this.prefix + "§cDu besitzt keine Freundesanfragen.");
                }
            } else {
                this.sendFriendhelp(player, 1);
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            String name = args[1];

            try {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                FriendManager friendTarget = new FriendManager(uuid);

                if (!player.getName().equalsIgnoreCase(name)) {
                    if (!(friendTarget.getRequests().contains(player.getUniqueId()))) {
                        if (!(friendManager.getFriends().contains(uuid))) {
                            if (!(friendManager.getRequests().contains(uuid))) {
                                if (friendTarget.can("Friendrequests")) {
                                    player.sendMessage(this.prefix + "Sende Anfrage...");
                                    friendTarget.addRequest(player.getUniqueId());
                                    player.sendMessage(this.prefix + "§aDu hast dem Spieler §7" + name + "§a eine Freundes Anfrage geschickt.");


                                    if (Main.getInstance().isPlayerOnline(uuid)) {
                                        Main.getInstance().sendMessage(uuid,this.prefix + "Der Spieler §e" + player.getName() + " §7möchte mit dir befreundet sein.");
                                        RedisBungee.getApi().sendChannelMessage("MESSAGE", uuid.toString() + ";" + "FRIENDACCEPT;" + player.getName());
                                    }
                                } else {
                                    player.sendMessage(this.prefix + "§cDieser Spieler nimmt keine Anfragen an.");
                                }
                            } else {
                                player.sendMessage(this.prefix + "§cDieser Spieler hat §4Dir §cbereits eine Anfrage gesendet.");
                            }
                        } else {
                            player.sendMessage(this.prefix + "§cDieser Spieler ist bereits in deiner Freundesliste vorhanden.");
                        }
                    } else {
                        player.sendMessage(this.prefix + "§cDu hast diesem Spieler bereits eine Anfrage gesendet.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDu kannst dir selbst keine Anfrage senden.");
                }

            } catch (Exception e) {
                player.sendMessage(this.prefix + "§cSpieler konnte nicht gefunden werden...");
                return;
            }


        } else if (args[0].equalsIgnoreCase("accept")) {
            String name = args[1];

            try {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                FriendManager friendTarget = new FriendManager(uuid);

                if (friendManager.getRequests().contains(uuid)) {
                    if (!(friendManager.getFriends().contains(uuid))) {
                        player.sendMessage(this.prefix + "Aktzeptiere Anfrage...");
                        friendManager.removeRequests(uuid);
                        friendTarget.addFriend(player.getUniqueId());
                        friendManager.addFriend(uuid);
                        player.sendMessage(this.prefix + "§aDie Freundesanfrage von §7" + name + " §awurde erfolgreich akzeptiert.");
                        Main.getInstance().sendMessageToSpigot(player.getUniqueId(), "RESET");

                        if (Main.getInstance().isPlayerOnline(uuid)) {
                            Main.getInstance().sendMessageToSpigot(uuid, "RESET");
                            Main.getInstance().sendMessage(uuid, this.prefix + "§aDer Spieler §7" + player.getName() + "§a hat deine Freundschaftsanfrage akzeptiert.");
                        }
                    } else {
                        player.sendMessage(this.prefix + "§cDu bist mit diesem Spieler bereits befreundet.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDieser Spieler hat dir keine Anfrage gesendet.");
                }

            } catch (Exception e) {
                player.sendMessage(this.prefix + "§cSpieler konnte nicht gefunden werden...");
                return;
            }
        } else if (args[0].equalsIgnoreCase("jump")) {
            String name = args[1];

            if (Main.getInstance().isPlayerOnline(name)) {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
                if (friendManager.getFriends().contains(uuid)) {
                    if (new FriendManager(uuid).can("Jump")) {
                        ServerInfo serverInfo_target = RedisBungee.getApi().getServerFor(uuid);
                        ServerInfo serverInfo = player.getServer().getInfo();
                        if ((serverInfo.getName().equalsIgnoreCase(serverInfo_target.getName()))) {
                            player.sendMessage(this.prefix + "§cDu befindest dich bereits auf diesen Server.");
                            return;
                        }
                        player.connect(serverInfo_target);
                        player.sendMessage(this.prefix + "Du wurdest auf den Server von §e" + name + "§7 geschoben. §7(§6" + serverInfo.getName() + "§7)");
                    } else {
                        player.sendMessage(this.prefix+ "§cDieser Spieler verweigert das Nachspringen von anderen Spielern.");
                    }
                    } else {
                    player.sendMessage(this.prefix + "§cDieser Spieler ist nicht in deiner Freundesliste!");
                }
            } else {
                player.sendMessage(this.prefix + "§cDieser Spieler ist nicht Online!");
            }
        } else if (args[0].equalsIgnoreCase("deny")) {
            String name = args[1];

            try {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);

                if (friendManager.getRequests().contains(uuid)) {
                    if (!(friendManager.getFriends().contains(uuid))) {
                        player.sendMessage(this.prefix + "Lösche Anfrage...");
                        friendManager.removeRequests(uuid);
                        player.sendMessage(this.prefix + "Die Freundesanfrage von §e" + name + " §7wurde erfolgreich §cabgelehnt.");


                        if (Main.getInstance().isPlayerOnline(uuid)) {
                            Main.getInstance().sendMessage(uuid, this.prefix + "Der Spieler §e" + player.getName() + "§7 hat deine Freundschaftsanfrage §cabgelehnt.");
                        }
                    } else {
                        player.sendMessage(this.prefix + "§cDu bist mit diesem Spieler bereits befreundet.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDieser Spieler hat dir keine Anfrage gesendet.");
                }

            } catch (Exception e) {
                player.sendMessage(this.prefix + "§cSpieler konnte nicht gefunden werden...");
                return;
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            String name = args[1];

                if (Main.getInstance().getPlayerDataBaseManager().getUUID(name) != null) {
                UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);

                if (friendManager.getFriends().contains(uuid)) {
                    player.sendMessage(this.prefix + "Lösche Freund...");
                    friendManager.removeFriend(uuid);
                    new FriendManager(uuid).removeFriend(player.getUniqueId());
                    player.sendMessage(this.prefix + "§aDu hast die Freundschaft mit §7" + Main.getInstance().getPlayerDataBaseManager().getName(uuid) + "§a aufgelöst.");

                    if (Main.getInstance().isPlayerOnline(uuid)) {
                        Main.getInstance().sendMessage(uuid,this.prefix + "Der Spieler §e" + player.getName() + "§7 hat die Freundschaft aufgelöst.");
                    }
                } else {
                    player.sendMessage(this.prefix + "§cDieser Spieler befindet sich nicht in deiner Freundesliste.");
                }
            } else {
                player.sendMessage(this.prefix + "§cDieser Spieler konnte nicht in der Datenbank gefunden werden.");
                return;
            }
        }
    }

    private void sendFriendhelp(final ProxiedPlayer proxiedPlayer, final int page) {
        proxiedPlayer.sendMessage("§8§m------§4 Freunde §8§m------");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§7/friend add <Spieler>");
        proxiedPlayer.sendMessage("§7/friend remove <Spieler>");
        proxiedPlayer.sendMessage("§7/friend list");
        proxiedPlayer.sendMessage("§7/friend accept <Spieler>");
        proxiedPlayer.sendMessage("§7/friend requests");
        proxiedPlayer.sendMessage("§7/friend acceptall");
        proxiedPlayer.sendMessage("§7/friend deny <Spieler>");
        proxiedPlayer.sendMessage("§7/friend denyall");
        proxiedPlayer.sendMessage("§7/friend jump <Spieler>");
        proxiedPlayer.sendMessage("§7/friend clear");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§8§m------§4 Freunde §8§m------");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        // stats kitsg Burnico all


        if (strings.length == 1) {
            ArrayList<String> list = new ArrayList<>();

            ArrayList<String> commands = new ArrayList<>();
            commands.add("add");
            commands.add("remove");
            commands.add("list");
            commands.add("requests");
            commands.add("jump");
            commands.add("acceptall");
            commands.add("accept");
            commands.add("deny");
            commands.add("clear");
            commands.add("denyall");

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
