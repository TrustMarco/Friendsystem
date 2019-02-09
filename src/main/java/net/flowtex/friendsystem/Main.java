package net.flowtex.friendsystem;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import net.flowtex.friendsystem.broadcaster.BroadcastManager;
import net.flowtex.friendsystem.chatfilter.ChatFilterManager;
import net.flowtex.friendsystem.commands.*;
import net.flowtex.friendsystem.listeners.ChatListener;
import net.flowtex.friendsystem.listeners.MessageListener;
import net.flowtex.friendsystem.listeners.ServerConnectListener;
import net.flowtex.friendsystem.listeners.ServerDisconnectListener;
import net.flowtex.friendsystem.partymanager.Vars;
import net.flowtex.friendsystem.playerdatabasemanager.PlayerDataBaseManager;
import net.flowtex.friendsystem.redismanager.RedisManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * ---------------------------------------------------
 * <p>
 * Projekt: [Flowtex.eu] Friendsystem
 * Author: Marco Weiß (Burnico)
 * Datum: 02.01.2019
 * Zeit: 15:19
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

public class Main extends Plugin {

    private static Main instance;

    private String prefix;

    private RedisManager redisManager;
    private Vars vars;
    private BroadcastManager broadcastManager;
    private ChatFilterManager chatFilterManager;
    public HashMap<UUID, Boolean> can_send = new HashMap<UUID, Boolean>();
    public HashMap<String, String> redo = new HashMap<String, String>();
    private PlayerDataBaseManager playerDataBaseManager;


    @Override
    public void onEnable() {
        this.init();
    }

    @Override
    public void onDisable() {
        getRedisManager().disconnect();
    }

    private void init() {
        setInstance(this);
        setPrefix("§e§lKrentoxNET §8× §7");
        this.redisManager = new RedisManager();
        this.vars = new Vars();
        this.broadcastManager = new BroadcastManager();
        this.chatFilterManager = new ChatFilterManager();
        this.playerDataBaseManager = new PlayerDataBaseManager();

        registerCommands();
        registerListeners();

        this.loadConfig();
        getRedisManager().connect();
        if (!(this.getBroadcastManager().existsTime())) {
            this.getBroadcastManager().setTime("360");
        }
        this.getBroadcastManager().startBroadcaster();
        ProxyServer.getInstance().registerChannel("Return");
        RedisBungee.getApi().registerPubSubChannels("MESSAGE", "SPIGOT_MESSAGE");
    }

    private void registerListeners() {
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        pluginManager.registerListener(this, new ServerConnectListener());
        pluginManager.registerListener(this, new ServerDisconnectListener());
        pluginManager.registerListener(this, new MessageListener());
        pluginManager.registerListener(this, new ChatListener());
    }

    private void registerCommands() {
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        pluginManager.registerCommand(this, new FriendCommand());
        pluginManager.registerCommand(this, new PartyCommand());
        pluginManager.registerCommand(this, new MessageCommand());
        pluginManager.registerCommand(this, new RedoCommand());
        pluginManager.registerCommand(this, new BroadcasterCommand());
        pluginManager.registerCommand(this, new ChatFilterCommand());
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatFilterManager getChatFilterManager() {
        return chatFilterManager;
    }

    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Vars getVars() {
        return vars;
    }

    public static void setInstance(Main instance) {
        Main.instance = instance;
    }

    public static Main getInstance() {
        return instance;
    }

    public PlayerDataBaseManager getPlayerDataBaseManager() {
        return playerDataBaseManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public void loadConfig()
    {
        File file = new File(this.getDataFolder().getPath(), "redis.yml");
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                configuration.set("host", "127.0.0.1");
                configuration.set("port", "6379");
                configuration.set("password", "Passwort");
                configuration.set("database", "1");

                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToSpigot(UUID uuid, String msg) {
        if (this.isPlayerOnline(uuid)) {
            RedisBungee.getApi().sendChannelMessage("SPIGOT_MESSAGE", uuid.toString() + ";" + msg);
        }

    }

    public boolean isPlayerOnline(final UUID uuid) {
        return RedisBungee.getApi().getPlayersOnline().contains(uuid);
    }

    public boolean isPlayerOnline(final String name) {
        UUID uuid = this.getPlayerDataBaseManager().getUUID(name);
        return this.isPlayerOnline(uuid);
    }

    public void sendMessage(final UUID uuid, final String msg) {
        if (this.isPlayerOnline(uuid)) {
            RedisBungee.getApi().sendChannelMessage("MESSAGE", uuid.toString() + ";" + msg);
        }
    }

    public List<String> getPlayersOnline() {
        List<String> result = new ArrayList<>();
        for (UUID uuid : RedisBungee.getApi().getPlayersOnline()) {
            result.add(RedisBungee.getApi().getNameFromUuid(uuid));
        }
        return result;
    }
}
