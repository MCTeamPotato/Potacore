package com.teampotato.potacore.data;

import com.teampotato.potacore.Potacore;
import com.teampotato.potacore.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PotatoEntityData extends SavedData {
    private final Map<UUID, Map<String, Object>> entityData = new ConcurrentHashMap<>();

    public static @NotNull PotatoEntityData get(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PotatoEntityData::load, PotatoEntityData::new, "potato_entity_data");
    }

    public static @NotNull PotatoEntityData load(CompoundTag tag) {
        PotatoEntityData data = new PotatoEntityData();
        data.loadData(tag);
        return data;
    }

    private void loadData(@NotNull CompoundTag tag) {
        entityData.clear();
        for (String uuidStr : tag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                CompoundTag entityTag = tag.getCompound(uuidStr);
                Map<String, Object> dataMap = new ConcurrentHashMap<>();
                for (String key : entityTag.getAllKeys()) {
                    Object value = NBTUtils.fromTag(entityTag.get(key));
                    if (value != null) {
                        dataMap.put(key, value);
                    } else {
                        Potacore.LOGGER.error("Unsupported tag type for key '{}' in UUID '{}'", key, uuidStr);
                    }
                }
                if (!dataMap.isEmpty()) entityData.put(uuid, dataMap);
            } catch (IllegalArgumentException e) {
                Potacore.LOGGER.error("Invalid UUID in saved data: {}", uuidStr, e);
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        entityData.forEach((uuid, dataMap) -> {
            if (dataMap.isEmpty()) return;

            CompoundTag entityTag = new CompoundTag();
            dataMap.forEach((key, value) -> {
                Tag tag = NBTUtils.toTag(value);
                if (tag != null) {
                    entityTag.put(key, tag);
                } else {
                    Potacore.LOGGER.error("Unsupported value type for key '{}' in UUID '{}'", key, uuid);
                }
            });
            compoundTag.put(uuid.toString(), entityTag);
        });
        return compoundTag;
    }

    public void setData(@NotNull UUID entity, @NotNull String key, @NotNull Object value) {
        entityData.computeIfAbsent(entity, id -> new ConcurrentHashMap<>()).put(key, value);
        setDirty();
    }

    public void setDataMap(@NotNull UUID entity, @NotNull Map<String, Object> dataMap) {
        entityData.put(entity, new ConcurrentHashMap<>(dataMap));
        setDirty();
    }

    public void removeData(@NotNull UUID entity, @NotNull String key) {
        Map<String, Object> map = entityData.get(entity);
        if (map != null) {
            map.remove(key);
            setDirty();
        }
    }

    public void removeData(@NotNull UUID entity) {
        if (entityData.remove(entity) != null) {
            setDirty();
        }
    }

    public @NotNull Map<String, Object> getDataMap(@NotNull UUID uuid) {
        return entityData.computeIfAbsent(uuid, u -> new ConcurrentHashMap<>());
    }

    public @Nullable Object getData(@NotNull UUID entity, @NotNull String key) {
        Map<String, Object> map = entityData.get(entity);
        return (map != null) ? map.get(key) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getAs(UUID uuid, String key, @NotNull Class<T> type) {
        Object val = getData(uuid, key);
        return type.isInstance(val) ? (T) val : null;
    }

    public @Nullable String getString(UUID uuid, String key) { return getAs(uuid, key, String.class); }
    public @Nullable Integer getInt(UUID uuid, String key) { return getAs(uuid, key, Integer.class); }
    public @Nullable Double getDouble(UUID uuid, String key) { return getAs(uuid, key, Double.class); }
    public @Nullable Float getFloat(UUID uuid, String key) { return getAs(uuid, key, Float.class); }
    public @Nullable Long getLong(UUID uuid, String key) { return getAs(uuid, key, Long.class); }
    public @Nullable Short getShort(UUID uuid, String key) { return getAs(uuid, key, Short.class); }
    public @Nullable Byte getByte(UUID uuid, String key) { return getAs(uuid, key, Byte.class); }
    public @Nullable ListTag getList(UUID uuid, String key) { return getAs(uuid, key, ListTag.class); }
    public @Nullable CompoundTag getCompound(UUID uuid, String key) { return getAs(uuid, key, CompoundTag.class); }

    public void clearAll() {
        this.entityData.clear();
        setDirty();
    }
}
