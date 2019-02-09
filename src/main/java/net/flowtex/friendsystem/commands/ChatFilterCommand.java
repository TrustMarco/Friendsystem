package net.flowtex.friendsystem.commands;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatFilterCommand extends Command {

    public ChatFilterCommand() { super("chatfilter", null);}

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Main.getInstance().getPrefix() + "§cDies ist hier nicht möglich");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (player.hasPermission("bungeesystem.command.chatfilter")) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    String word = args[1].toLowerCase();
                    Integer ID = Integer.parseInt(args[2]);

                    if (!(Main.getInstance().getChatFilterManager().existsWord(word))) {
                        player.sendMessage(Main.getInstance().getPrefix() + "Erstelle Wort...");
                        Main.getInstance().getChatFilterManager().addFilterWord(word, ID);
                        player.sendMessage(Main.getInstance().getPrefix() + "§aDu hast erfolgreich das Wort §a" + word + "§a zu den Filter Wörtern hinzugefügt");
                    } else {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieses Wort wurde bereits hinzugefügt.");
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    String word = args[1];

                    if (Main.getInstance().getChatFilterManager().existsWord(word)) {
                        Main.getInstance().getChatFilterManager().removeFilteredWord(word.toLowerCase());
                        player.sendMessage(Main.getInstance().getPrefix() + "§aDas Wort §7" + word + "§a wurde erfolgreich aus der Liste entnommen.");
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    player.sendMessage("§8§m------ §aChatFilter §8§m------");
                    player.sendMessage("");
                    for (String words : Main.getInstance().getChatFilterManager().getWords()) {
                        String word = words.split("/")[0];
                        String ID_string = words.split("/")[1];
                        player.sendMessage("§8» §7Wort§8: §e" + word + " §8| §7Mute-ID§8: §e" + ID_string);
                    }
                    player.sendMessage("");
                     player.sendMessage("§8§m------ §aChatFilter §8§m------");
                }
            } else {
                player.sendMessage("§8§m------§a ChatFilter §8§m------");
                player.sendMessage("");
                player.sendMessage("§7/chatfilter add <Wort> [Mute-ID]");
                player.sendMessage("§7/chatfilter remove <Wort>");
                player.sendMessage("§7/chatfilter list");
                player.sendMessage("");
                player.sendMessage("§8§m------§a ChatFilter §8§m------");
            }
        } else {
            player.sendMessage(Main.getInstance().getPrefix() + "§cDazu hast du keine Rechte!");
        }
    }
}
