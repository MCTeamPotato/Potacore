package com.teampotato.potacore.client;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class SimpleJsonConfig {
    private final Path configPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, Object> configMap = new Object2ObjectOpenHashMap<>();

    private SimpleJsonConfig(Path configPath) {
        try {
            this.configPath = configPath;
            this.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Optional<SimpleJsonConfig> create(Path configPath) {
        return Optional.ofNullable(FMLLoader.getDist().isClient() ? new SimpleJsonConfig(configPath) : null);
    }

    private void loadConfig() throws IOException {
        if (Files.exists(this.configPath)) {
            byte[] bytes = Files.readAllBytes(this.configPath);
            String json = new String(bytes, StandardCharsets.UTF_8);

            if (json.trim().isEmpty()) {
                this.configMap = new Object2ObjectOpenHashMap<>();
            } else {
                Map<String, Object> jsonMap = this.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                this.configMap = jsonMap == null ? new Object2ObjectOpenHashMap<>() : new Object2ObjectOpenHashMap<>(jsonMap);
            }
        } else {
            Files.createDirectories(this.configPath.getParent());
            Files.createFile(this.configPath);
            this.configMap = new Object2ObjectOpenHashMap<>();
            this.saveConfig();
        }
    }

    @CanIgnoreReturnValue
    public SimpleJsonConfig saveConfig() {
        try {
            String json = this.gson.toJson(this.configMap);
            Files.writeString(this.configPath, json);
            return this;
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

    @CanIgnoreReturnValue
    public SimpleJsonConfig put(String key, Object value) {
        this.configMap.put(key, value);
        return this;
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