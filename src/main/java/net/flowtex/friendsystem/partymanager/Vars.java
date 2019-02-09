package net.flowtex.friendsystem.partymanager;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 04.01.2019
 * Zeit: 19:26
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

public class Vars {

    public final HashMap<String, String> party_owner = new HashMap<>();
    public final HashMap<String, List<String>> party_members = new HashMap<>();
    public final HashMap<String, String> party = new HashMap<>();
    public final HashMap<String, List<String>> party_invite = new HashMap<>();

}
