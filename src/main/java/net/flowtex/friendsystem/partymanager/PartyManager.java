package net.flowtex.friendsystem.partymanager;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 04.01.2019
 * Zeit: 19:23
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

public class PartyManager {

    private String name;
    private HashMap<String, String> owner = Main.getInstance().getVars().party_owner;
    private HashMap<String, List<String>> member = Main.getInstance().getVars().party_members;

    public PartyManager (final String name) {
        this.name = name;
    }

    public boolean exists() {
        if (this.owner.containsKey(this.name)) return true;
        return false;
    }

    public void create(final String name) {
        this.owner.put(this.name, name);
        this.member.put(this.name, new ArrayList<>());
    }

    public void addMember(final String name) {
        List<String> members = this.member.get(this.name);
        members.add(name);
        this.member.put(this.name, members);
    }

    public void removeMember(final String name) {
        List<String> members = this.member.get(this.name);
        members.remove(name);
        this.member.put(this.name, members);
    }

    public boolean contains(final String name) {
        List<String> members = this.member.get(this.name);
        return members.contains(name);
    }

    public List<String> getMembers() {
        return this.member.get(this.name);
    }

    public void delete() {
        this.owner.remove(this.name);
        this.member.remove(this.name);
    }

    public String getOwner() {
        return this.owner.get(this.name);
    }

}
