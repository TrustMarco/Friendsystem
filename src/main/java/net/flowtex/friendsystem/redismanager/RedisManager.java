package net.flowtex.friendsystem.redismanager;

import net.flowtex.friendsystem.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 02.01.2019
 * Zeit: 15:28
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

public class RedisManager {

    public JedisPool jedis;

    public void disconnect()
    {
        this.jedis.close();
    }

    public void connect()
    {
        File file = new File(Main.getInstance().getDataFolder().getPath(), "redis.yml");
        try
        {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            String host = configuration.getString("host");
            String password = configuration.getString("password");
            String port = configuration.getString("port");
            String database = configuration.getString("database");

            //pool = new JedisPool(new JedisPoolConfig(), "212.224.88.169", 6379, 2000, "Pro_bessel3_Cumin_Iffy0_Bobcat0", 15);
            JedisPool jedis = new JedisPool(new JedisPoolConfig(), host, Integer.valueOf(port), 2000, password, Integer.valueOf(database));
            this.jedis = jedis;
            System.out.println("Connection zu Redis wurde erfolgreich aufgebaut! Host: " + host);
            System.out.println("[Redis] Server Ping: " + jedis.getResource().ping());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
