package com.xsasakihaise.hellaselo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HellasEloInfoConfig {

    private transient final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile;

    private String version = null;
    private String[] dependencies = new String[0];
    private String[] features = new String[0];

    private boolean valid = false;

    /**
     * Lädt die Serverdatei wenn vorhanden, ansonsten versucht die gebündelte Resource.
     */
    public void load(File serverRoot) {
        File configDir = new File(serverRoot, "config/hellas/elo/");
        if (!configDir.exists()) configDir.mkdirs();

        configFile = new File(configDir, "hellas_elo_info.json");

        // 1) Versuche Serverdatei
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                HellasEloInfoConfig loaded = gson.fromJson(reader, HellasEloInfoConfig.class);
                if (loaded != null) {
                    this.version = loaded.version;
                    this.dependencies = loaded.dependencies != null ? loaded.dependencies : new String[0];
                    this.features = loaded.features != null ? loaded.features : new String[0];
                    this.valid = true;
                    return;
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                this.valid = false;
                return;
            }
        }

        // 2) Falls beim Start schon Defaults aus Resource geladen wurden und valid==true, nichts weiter tun
        if (this.valid) return;

        // 3) Versuche gebündelte Resource vom Classpath (resources/config/hellaselo.json)
        loadDefaultsFromResource();
    }

    /**
     * Lädt die gebündelte Resource `config/hellaselo.json` vom Classpath.
     * Wird beim Plugin-Konstruktor aufgerufen, damit Commands sofort korrekte Werte haben.
     */
    public void loadDefaultsFromResource() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/hellaselo.json")) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    HellasEloInfoConfig loaded = gson.fromJson(isr, HellasEloInfoConfig.class);
                    if (loaded != null) {
                        this.version = loaded.version;
                        this.dependencies = loaded.dependencies != null ? loaded.dependencies : new String[0];
                        this.features = loaded.features != null ? loaded.features : new String[0];
                        this.valid = true;
                    }
                }
            } else {
                this.valid = false;
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            this.valid = false;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return version;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public String[] getFeatures() {
        return features;
    }
}