package com.teampotato.potacore.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages a JSON-based configuration file with simple key-value storage.
 * This class handles loading, saving, and reloading configurations from/to a JSON file,
 * and provides type-safe methods to access configuration values. If the configuration file
 * or parent directories do not exist, they are automatically created.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * SimpleJsonConfig config = new SimpleJsonConfig(Paths.get("config/config.json"));
 * config.put("enableFeature", true);
 * config.saveConfig();
 * boolean featureEnabled = config.get("enableFeature", Boolean.class);
 * }
 * </pre>
 *
 * <p><b>Thread Safety:</b> This class is not thread-safe. External synchronization is required
 * if used in concurrent environments.</p>
 *
 */
public class SimpleJsonConfig {
    private final Path configPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, Object> configMap = new HashMap<>();

    /**
     * Constructs a new configuration for the specified file path.
     * Automatically loads existing configuration or initializes a new file if missing.
     *
     * @param configPath Path to the JSON configuration file (e.g., {@code Paths.get("config/settings.json")})
     * @throws RuntimeException If file/directory creation fails or JSON parsing errors occur during initial load.
     */
    public SimpleJsonConfig(Path configPath) {
        try {
            this.configPath = configPath;
            this.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConfig() throws IOException {
        if (Files.exists(this.configPath)) {
            byte[] bytes = Files.readAllBytes(this.configPath);
            String json = new String(bytes, StandardCharsets.UTF_8);

            if (json.trim().isEmpty()) {
                this.configMap = new HashMap<>();
            } else {
                this.configMap = this.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                if (this.configMap == null) {
                    this.configMap = new HashMap<>();
                }
            }
        } else {
            Files.createDirectories(this.configPath.getParent());
            Files.createFile(this.configPath);
            this.configMap = new HashMap<>();
            this.saveConfig();
        }
    }

    /**
     * Saves the current configuration state to the JSON file.
     *
     * @throws RuntimeException If writing to the file fails (e.g., I/O errors or insufficient permissions).
     */
    public void saveConfig() {
        try {
            String json = this.gson.toJson(this.configMap);
            Files.writeString(this.configPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reloads the configuration from disk, discarding any unsaved changes.
     *
     * @throws RuntimeException If file reading or JSON parsing fails during reload.
     */
    public void reloadConfig() {
        try {
            this.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores a key-value pair in the configuration and immediately persists to disk.
     *
     * @param key   Configuration key (case-sensitive)
     * @param value Configuration value (must be serializable by Gson)
     */
    public void put(String key, Object value) {
        this.configMap.put(key, value);
        this.saveConfig();
    }

    /**
     * Retrieves a configuration value without explicit type checking.
     * Returns {@code null} if the key does not exist. <b>Warning:</b> Type mismatches
     * will throw {@link ClassCastException} at runtime.
     *
     * @param key Configuration key to retrieve
     * @param <T> Inferred return type (unsafe - no compile-time validation)
     * @return Value associated with the key, or {@code null} if missing
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) this.configMap.get(key);
    }

    /**
     * Type-safe method to retrieve a configuration value.
     * Returns {@code null} if the key does not exist or the value type mismatches.
     *
     * @param key  Configuration key to retrieve
     * @param type Expected class of the return value (e.g., {@code Boolean.class})
     * @param <T>  Type of the expected return value
     * @return Value cast to the requested type, or {@code null} if missing/type-mismatched
     */
    @Nullable
    public <T> T get(String key, Class<T> type) {
        Object value = this.configMap.get(key);
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }
}
