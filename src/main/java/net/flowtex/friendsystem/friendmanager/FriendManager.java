package net.flowtex.friendsystem.friendmanager;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.commands.MultiKeyJedisClusterCommands;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 02.01.2019
 * Zeit: 15:42
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

public class FriendManager {

    private UUID uuid;

    public FriendManager(final UUID uuid) {
        this.uuid = uuid;
    }

    public List<UUID> getFriends() {
        if (this.existsFriends()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            String get = jedis.get("bungeesystem:friends:players:" + uuid.toString() + ":friends");
            jedis.close();
            if (get.contains(";")) {
                List<UUID> list = new ArrayList<UUID>();
                for (String string_uuid : get.split(";")) {
                    list.add(UUID.fromString(string_uuid));
                }
                return list;
            } else {
                List<UUID> list = new ArrayList<UUID>();
                list.add(UUID.fromString(get));
                return list;
            }
        }
        return new ArrayList<UUID>();
    }

    public void addFriend(final UUID uuid) {
        if (this.existsFriends()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            String get = jedis.get("bungeesystem:friends:players:" + this.uuid.toString() + ":friends");
            String set = get + ";" + uuid.toString();
            jedis.set("bungeesystem:friends:players:" + this.uuid.toString() + ":friends", set);
            jedis.close();
        } else {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            jedis.set("bungeesystem:friends:players:" + this.uuid.toString() + ":friends", uuid.toString());
            jedis.close();
        }
    }

    public void sendServerToBukkit(final String server) {
        Main.getInstance().can_send.remove(this.uuid);
        for (UUID uuid : this.getFriends()) {
            if (!(Main.getInstance().can_send.containsKey(this.uuid))) {
                if (Main.getInstance().isPlayerOnline(uuid)) {
                    Main.getInstance().sendMessageToSpigot(uuid, "SERVER=" + this.uuid + "=" + server);
                }
            } else {
                return;
            }
        }
    }

    public void sendStateToBukkit() {
        Main.getInstance().can_send.remove(this.uuid);
        for (UUID uuid : this.getFriends()) {
            if (!(Main.getInstance().can_send.containsKey(this.uuid))) {
                if (Main.getInstance().isPlayerOnline(uuid)) {
                    Main.getInstance().sendMessageToSpigot(uuid, "OFFLINE=" + this.uuid);
                }
            } else {
                return;
            }
        }
    }

    public void setLast() {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("Europe/Berlin"));

        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        int minute = date.getMinute();
        int hour = date.getHour();

        String set = day + ";" + month + ";" + year + ";" + minute + ";" + hour;
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.set("bungeesystem:friends:players:" + this.uuid.toString() + ":time", set);
        jedis.close();
    }

    public boolean existsFriends() {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();

        if (jedis.exists("bungeesystem:friends:players:" + uuid.toString() + ":friends")) {
            jedis.close();
            return true;
        }
        jedis.close();
        return false;
    }

    public boolean existsRequests() {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        if (jedis.exists("bungeesystem:friends:players:" + uuid.toString() + ":requests")) {
            jedis.close();
            return true;
        }
        jedis.close();
        return false;
    }

    public void set(final String path, final String state) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.set("bungeesystem:friends:players:" + uuid.toString() + ":" + path, state);
        jedis.close();
    }

    public boolean can(final String path) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        if (jedis.exists("bungeesystem:friends:players:" + uuid.toString() + ":" + path)) {
            if (jedis.get("bungeesystem:friends:players:" + uuid.toString() + ":" + path).equalsIgnoreCase("false")) {
                jedis.close();
                return false;
            }
        }
        jedis.close();
        return true;
    }

    public int getOnlineFriendCount() {
        if (!(this.getFriends().isEmpty())) {
            int result = 0;
            for (UUID uuid : this.getFriends()) {
                if (Main.getInstance().isPlayerOnline(uuid)) {
                    result++;
                }
            }
            return result;
        }
        return 0;
    }

    public void clear() {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.del("bungeesystem:friends:players:" + uuid.toString() + ":friends");
        jedis.close();
        if (ProxyServer.getInstance().getPlayer(this.uuid) != null) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(this.uuid);
            Main.getInstance().sendMessageToSpigot(player.getUniqueId(), "RESET");
        }
    }

    public List<UUID> getRequests() {
        if (this.existsRequests()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            String get = jedis.get("bungeesystem:friends:players:" + uuid.toString() + ":requests");
            jedis.close();
            if (get.contains(";")) {
                List<UUID> list = new ArrayList<UUID>();
                for (String string_uuid : get.split(";")) {
                    list.add(UUID.fromString(string_uuid));
                }
                return list;
            } else {
                List<UUID> list = new ArrayList<UUID>();
                list.add(UUID.fromString(get));
                return list;
            }
        }
        return new ArrayList<UUID>();
    }



    public void addRequest(final UUID uuid) {
        if (this.existsRequests()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            String get = jedis.get("bungeesystem:friends:players:" + this.uuid.toString() + ":requests");
            String set = get + ";" + uuid.toString();
            Main.getInstance().getRedisManager().jedis.getResource().set("bungeesystem:friends:players:" + this.uuid.toString() + ":requests", set);
            jedis.close();
        } else {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            jedis.set("bungeesystem:friends:players:" + this.uuid.toString() + ":requests", uuid.toString());
            jedis.close();
        }
    }

    public void removeRequests(final UUID uuid) {
        if (this.existsRequests()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            String get = jedis.get("bungeesystem:friends:players:" + this.uuid.toString() + ":requests");
            if (!get.contains(";")) {
                Main.getInstance().getRedisManager().jedis.getResource().del("bungeesystem:friends:players:" + this.uuid.toString() + ":requests");
            } else {
                List<UUID> list = new ArrayList<UUID>();

                for (String string_uuid : get.split(";")) {
                    if (!(string_uuid.equals(uuid.toString()))) {
                        list.add(UUID.fromString(string_uuid));
                    }
                }
                jedis.set("bungeesystem:friends:players:" + this.uuid.toString() + ":requests", list.toString().replace("[", "").replace("]", "").replace(", ", ";"));
            }
            jedis.close();
        }
    }

    public void removeFriend(final UUID uuid) {
        if (this.existsFriends()) {
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            Pipeline pipeline = jedis.pipelined();
            String get = jedis.get("bungeesystem:friends:players:" + this.uuid.toString() + ":friends");
            if (!get.contains(";")) {
                pipeline.del("bungeesystem:friends:players:" + this.uuid.toString() + ":friends");
            } else {
                List<UUID> list = new ArrayList<UUID>();

                for (String string_uuid : get.split(";")) {
                    if (!(string_uuid.equals(uuid.toString()))) {
                        list.add(UUID.fromString(string_uuid));
                    }
                }
                pipeline.set("bungeesystem:friends:players:" + this.uuid.toString() + ":friends", list.toString().replace("[", "").replace("]", "").replace(", ", ";"));
            }
            pipeline.sync();
            jedis.close();
            if (ProxyServer.getInstance().getPlayer(this.uuid) != null) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(this.uuid);
                Main.getInstance().sendMessageToSpigot(player.getUniqueId(), "RESET");
            }
        }
    }

}
