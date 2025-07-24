package com.teampotato.potacore.data;

import com.teampotato.potacore.Potacore;
import com.teampotato.potacore.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PotatoEntityData extends SavedData {
    private final Map<String, Object> entityData = new ConcurrentHashMap<>();

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
                for (String key : entityTag.getAllKeys()) {
                    Object value = NBTUtils.fromTag(entityTag.get(key));
                    if (value != null) {
                        entityData.put(createKey(uuid, key), value);
                    } else {
                        Potacore.LOGGER.error("Unsupported tag type for key '{}' in UUID '{}'", key, uuidStr);
                    }
                }
            } catch (IllegalArgumentException e) {
                Potacore.LOGGER.error("Invalid UUID in saved data: {}", uuidStr, e);
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        entityData.forEach((compositeKey, value) -> {
            String[] parts = compositeKey.split(":");
            if (parts.length == 2) {
                UUID uuid = UUID.fromString(parts[0]);
                String key = parts[1];
                CompoundTag entityTag = new CompoundTag();
                Tag tag = NBTUtils.toTag(value);
                if (tag != null) {
                    entityTag.put(key, tag);
                    compoundTag.put(uuid.toString(), entityTag);
                } else {
                    Potacore.LOGGER.error("Unsupported value type for key '{}' in UUID '{}'", key, uuid);
                }
            }
        });
        return compoundTag;
    }

    @Contract(pure = true)
    private @NotNull String createKey(@NotNull UUID uuid, String key) {
        return uuid + ":" + key;
    }

    public void setData(@NotNull UUID entity, @NotNull String key, @NotNull Object value) {
        entityData.put(createKey(entity, key), value);
        setDirty();
    }

    public void setDataMap(@NotNull UUID entity, @NotNull Map<String, Object> dataMap) {
        dataMap.forEach((key, value) -> setData(entity, key, value));
        setDirty();
    }

    public void removeData(@NotNull UUID entity, @NotNull String key) {
        entityData.remove(createKey(entity, key));
        setDirty();
    }

    public void removeData(@NotNull UUID entity) {
        entityData.keySet().removeIf(key -> key.startsWith(entity.toString()));
        setDirty();
    }

    public @Nullable Object getData(@NotNull UUID entity, @NotNull String key) {
        return entityData.get(createKey(entity, key));
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getAs(UUID uuid, String key, @NotNull Class<T> type) {
        Object val = getData(uuid, key);
        return type.isInstance(val) ? (T) val : null;
    }

    public Optional<String> getString(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, String.class));}
    public Optional<Integer> getInt(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Integer.class));}
    public Optional<Double> getDouble(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Double.class));}
    public Optional<Float> getFloat(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Float.class));}
    public Optional<Long> getLong(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Long.class));}
    public Optional<Short> getShort(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Short.class));}
    public Optional<Byte> getByte(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, Byte.class));}
    public Optional<ListTag> getList(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, ListTag.class));}
    public Optional<CompoundTag> getCompound(UUID uuid, String key) {return Optional.ofNullable(getAs(uuid, key, CompoundTag.class));}

    public void clearAll() {
        this.entityData.clear();
        setDirty();
    }
}
