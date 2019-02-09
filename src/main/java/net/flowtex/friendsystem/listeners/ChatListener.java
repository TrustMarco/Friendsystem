package net.flowtex.friendsystem.listeners;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import jdk.Exported;
import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            String message = e.getMessage().toLowerCase();
            for (String words : Main.getInstance().getChatFilterManager().getWords()) {
                String word = words.split("/")[0];

                if (message.contains(word.toLowerCase())) {
                    if (!(message.startsWith("/chatfilter"))) {
                        e.setCancelled(true);
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieses Wort darfst du leider nicht verwenden.");
                        sendMessage(player.getName(), word, Integer.parseInt(words.split("/")[1]), e.getMessage());
                        return;
                    }
                }
            }
        }
    }

    private void sendMessage(final String player, final String word, final Integer ID, final String message) {
        TextComponent accept = new TextComponent("§8«§aMUTEN§8»");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mute " + player + " " + ID));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJa, ich möchte!").create()));

        for (UUID uuid : RedisBungee.getApi().getPlayersOnline()) {

            Main.getInstance().sendMessage(uuid,"§8§m------ §aChatFilter §8§m------");
            Main.getInstance().sendMessage(uuid,"");
            Main.getInstance().sendMessage(uuid, "§8» §7Nachricht§8: §e" + message);
            Main.getInstance().sendMessage(uuid,"§8» §7Gefiltertes Wort§8: §e" + word);
            Main.getInstance().sendMessage(uuid,"§8» §7Spieler§8: §e" + player);
            Main.getInstance().sendMessage(uuid, "");
            //Main.getInstance().sendMessage(accept);
            Main.getInstance().sendMessage(uuid,"");
            Main.getInstance().sendMessage(uuid, "§8§m------ §aChatFilter §8§m------");

        }
    }

}
