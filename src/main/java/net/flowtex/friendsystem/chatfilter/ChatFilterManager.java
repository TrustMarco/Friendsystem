package net.flowtex.friendsystem.chatfilter;

import net.flowtex.friendsystem.Main;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;

public class ChatFilterManager {

    private HashMap<String, Integer> words = new HashMap<>();

    public void addFilterWord(final String word, final Integer ID) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.set("bungeesystem:chatfilter:words:" + word, String.valueOf(ID));
        jedis.close();
        words.put(word, ID);
    }

    public void removeFilteredWord(final String word) {
        Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
        jedis.del("bungeesystem:chatfilter:words:" + word);
        jedis.close();
        words.remove(word);
    }

    public boolean existsWord(final String word) {
        for (String words : this.getWords()) {
            if (words.split("/")[0].equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getWords() {
        if (words.isEmpty()) {
            List<String> list = new ArrayList<>();
            Jedis jedis = Main.getInstance().getRedisManager().jedis.getResource();
            for (String code : jedis.keys("bungeesystem:chatfilter:words:*")) {
                Integer ID = Integer.parseInt(jedis.get("bungeesystem:chatfilter:words:" + code.split(":")[3]));
                list.add(code.split(":")[3] + "/" + ID);
                words.put(code.split(":")[3], ID);
            }
            jedis.close();
            return list;
        } else {
            List<String> list = new ArrayList<>();
            for (String word : this.words.keySet()) {
                list.add(word + "/" + this.words.get(word));
            }
            return list;
        }
    }
}
