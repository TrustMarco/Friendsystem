package net.flowtex.friendsystem.partymanager;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 04.01.2019
 * Zeit: 19:48
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

public class PlayerManager {

    private String name;


    public PlayerManager(final String name) {
        this.name = name;
    }

    public void addInvite(final String party) {
            List<String> invites = this.getInvites();
            invites.add(party);
            Main.getInstance().getVars().party_invite.put(name, invites);
    }

    public List<String> getInvites() {
        if (Main.getInstance().getVars().party_invite.containsKey(name)) return (Main.getInstance().getVars().party_invite.get(name));
        return new ArrayList<>();
    }

    public void removeInvite(final String party) {
        if (this.getInvites().contains(party)) {
            List<String> invites = this.getInvites();
            invites.remove(party);
            Main.getInstance().getVars().party_invite.put(name, invites);
        }
    }

    public boolean isInParty() {
        if (Main.getInstance().getVars().party.containsKey(ProxyServer.getInstance().getPlayer(this.name))) {
            return true;
        } else {
            UUID uuid = Main.getInstance().getPlayerDataBaseManager().getUUID(name);
            Main.getInstance().sendMessage(uuid, Main.getInstance().getPrefix() + "§cDu befindest dich in keiner Party.");
            return false;
        }
    }

    public String getParty(final ProxiedPlayer player) {
        return Main.getInstance().getVars().party.get(name);
    }

}
