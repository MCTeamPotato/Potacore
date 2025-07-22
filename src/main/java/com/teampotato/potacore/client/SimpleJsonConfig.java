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

public class SimpleJsonConfig {
    private final Path configPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, Object> configMap = new HashMap<>();

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

    public void saveConfig() {
        try {
            String json = this.gson.toJson(this.configMap);
            Files.writeString(this.configPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        try {
            this.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, Object value) {
        this.configMap.put(key, value);
        this.saveConfig();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) this.configMap.get(key);
    }

    @Nullable
    public <T> T get(String key, Class<T> type) {
        Object value = this.configMap.get(key);
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }
}
