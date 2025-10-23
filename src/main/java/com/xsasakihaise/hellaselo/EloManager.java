package com.xsasakihaise.hellaselo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EloManager {

    private final Gson gson = new Gson();
    private final Map<String, Integer> eloMap = new HashMap<>();
    private final Map<String, String> usernameCache = new HashMap<>();

    private File dataFile;
    private File cacheFile;
    private EloConfig config;

    public EloManager(EloConfig config) {
        this.config = config;
    }

    public void loadData(File serverRoot) {
        File dataDir = new File(serverRoot, "config/hellas/elo/");
        if (!dataDir.exists()) dataDir.mkdirs();

        dataFile = new File(dataDir, "elodata.json");
        cacheFile = new File(serverRoot, "usernamecache.json");

        // Load Elo
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<Map<String, Integer>>(){}.getType();
                Map<String, Integer> map = gson.fromJson(reader, type);
                if (map != null) eloMap.putAll(map);
            } catch (IOException e) { e.printStackTrace(); }
        }

        // Load username cache
        if (cacheFile.exists()) {
            try (FileReader reader = new FileReader(cacheFile)) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> map = gson.fromJson(reader, type);
                if (map != null) usernameCache.putAll(map);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void saveData() {
        if (dataFile == null) return;
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(eloMap, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveUsernameCache() {
        if (cacheFile == null) return;
        try (FileWriter writer = new FileWriter(cacheFile)) {
            gson.toJson(usernameCache, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void addPlayerIfMissing(String name) {
        if (!usernameCache.containsValue(name)) {
            String uuid = UUID.randomUUID().toString();
            usernameCache.put(uuid, name);
        }
    }

    public void addMatch(String winner, String loser, int remaining, File serverRoot) {
        addPlayerIfMissing(winner);
        addPlayerIfMissing(loser);
        saveUsernameCache();

        int winnerElo = eloMap.getOrDefault(winner, 1000);
        int loserElo = eloMap.getOrDefault(loser, 1000);

        double expectedWinner = 1.0 / (1 + Math.pow(10, (loserElo - winnerElo) / 400.0));
        int delta = (int)((config.baseElo + remaining * config.remainingMultiplier) * (1 - expectedWinner));

        eloMap.put(winner, winnerElo + delta);
        eloMap.put(loser, loserElo - delta);

        saveData();
        System.out.println("[HellasElo] " + winner + " won vs " + loser + " | +" + delta + " Elo");
    }

    public Map<String, Integer> getLeaderboard() { return eloMap; }

    public String[] getAllUsernames() { return usernameCache.values().toArray(new String[0]); }
}
