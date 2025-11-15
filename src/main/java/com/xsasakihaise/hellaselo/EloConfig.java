package com.xsasakihaise.hellaselo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple JSON-backed configuration holder for the Elo system.
 * <p>
 * The config currently exposes the base K-factor and the multiplier applied for each
 * remaining Pokémon the winner preserves. The values are stored under
 * {@code config/hellas/elo/hellas_elo_config.json} within the server root.
 * </p>
 */
public class EloConfig {

    private transient final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile;

    public int baseElo = 50; // K-factor
    public int remainingMultiplier = 10;

    /**
     * Loads an existing JSON configuration or writes a default one if none exists yet.
     *
     * @param serverRoot directory that contains the {@code config} folder.
     */
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

    /**
     * Persists the current configuration object to disk.
     * Callers must have invoked {@link #loadConfig(File)} first so {@link #configFile}
     * is initialised.
     */
    public void saveConfig() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}