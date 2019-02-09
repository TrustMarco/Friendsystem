package net.flowtex.friendsystem.broadcaster;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.flowtex.friendsystem.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Krentox.net] Broadcaster
 * Author: Marco Weiß (Burnico)
 * Datum: 01.12.2018
 * Zeit: 20:16
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

public class BroadcastManager {


    public Integer count = 0;
    public ScheduledTask sched;
    // broadcast:messages:

    public void setMessage(final String name, final String message) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.set("bungeesystem:broadcast:messages:" + name, message);
        jedis.close();
    }

    public List<String> getMessages() {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        List<String> result = new ArrayList<>();
        for (String code :  jedis.keys("bungeesystem:broadcast:messages:*")) {
            result.add(code.split(":")[3]);
        }
        jedis.close();
        return result;
    }

    public String getMessage(final String name) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        String get = jedis.get("bungeesystem:broadcast:messages:" + name);
        jedis.close();
        return get;
    }

    public void removeMessage(String name) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
         jedis.del("bungeesystem:broadcast:messages:" + name);
         jedis.close();
    }


    public void setTime(final String seconds) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
         jedis.set("bungeesystem:broadcast:time", seconds);
    }

    public String getTime() {
        if (!this.existsTime()) return "20";
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        String time = jedis.get("bungeesystem:broadcast:time");
        jedis.close();
        return time;
    }

    public boolean existsTime() {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        if (jedis.exists("bungeesystem:broadcast:time")) {
            jedis.close();
            return true;
        }
        jedis.close();
        return false;
    }

    public boolean exist(final String name) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        if (jedis.exists("bungeesystem:broadcast:messages:" + name)) {
            jedis.close();
            return true;
        }
        jedis.close();
        return false;
    }


     public void startBroadcaster() {
        if (sched != null) {
            ProxyServer.getInstance().getScheduler().cancel(sched);
        }
         Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        sched = ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<String> messages = getMessages();
                for (UUID uuid : RedisBungee.getApi().getPlayersOnline()) {

                           Main.getInstance().sendMessage(uuid, getMessage(messages.get(count)).replace("&", "§"));
                }

                if ((count+1) == (Integer.valueOf(jedis.keys("bungeesystem:broadcast:messages:*").size()))) {
                    count = 0;
                } else {
                    count++;
                }
            }
        }, 3, Integer.parseInt(this.getTime()), TimeUnit.SECONDS);
    }



}
