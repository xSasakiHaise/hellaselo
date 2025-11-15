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

/**
 * Metadata file that describes the Elo module (version, dependencies, advertised features).
 * <p>
 * The data is primarily used for staff information commands and therefore attempts to load
 * quickly during construction from an embedded resource before falling back to disk.
 * </p>
 */
public class HellasEloInfoConfig {

    private transient final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile;

    private String version = null;
    private String[] dependencies = new String[0];
    private String[] features = new String[0];

    private boolean valid = false;

    /**
     * Loads the server-side metadata file if present or falls back to the bundled resource.
     *
     * @param serverRoot server directory that contains the config folder.
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
     * Loads the bundled {@code config/hellaselo.json} metadata from the classpath.
     * Invoked during plugin construction so commands can already display version info.
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

    /**
     * @return {@code true} if meaningful metadata has been loaded.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Persists the metadata file to disk for operators that want to override defaults.
     */
    public void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return semantic version string or {@code null} if none was defined.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return list of dependent Hellas modules this Elo system expects.
     */
    public String[] getDependencies() {
        return dependencies;
    }

    /**
     * @return human readable bullet points that describe this module.
     */
    public String[] getFeatures() {
        return features;
    }
}