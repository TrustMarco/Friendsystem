package net.flowtex.friendsystem.playerdatabasemanager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.flowtex.friendsystem.Main;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class PlayerDataBaseManager {


    public UUID getUUID(String name) {
        return RedisBungee.getApi().getUuidFromName(name);
    }



    public String getName(UUID uuid) {
        return RedisBungee.getApi().getNameFromUuid(uuid);
    }
}
