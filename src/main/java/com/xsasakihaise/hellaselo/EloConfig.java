package com.xsasakihaise.hellaselo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EloConfig {

    private transient final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile;

    public int baseElo = 50; // K-factor
    public int remainingMultiplier = 10;

    public void loadConfig(File serverRoot) {
        File configDir = new File(serverRoot, "config/hellas/elo/");
        if (!configDir.exists()) configDir.mkdirs();

        configFile = new File(configDir, "hellas_elo_config.json");
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                EloConfig loaded = gson.fromJson(reader, EloConfig.class);
                if (loaded != null) {
                    this.baseElo = loaded.baseElo;
                    this.remainingMultiplier = loaded.remainingMultiplier;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveConfig();
        }
    }

    public void saveConfig() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}