package com.teampotato.potacore.util;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

public class NBTUtils {
    public static @Nullable Object fromTag(@Nullable Tag tag) {
        if (tag instanceof StringTag) return tag.getAsString();
        if (tag instanceof IntTag) return ((IntTag) tag).getAsInt();
        if (tag instanceof DoubleTag) return ((DoubleTag) tag).getAsDouble();
        if (tag instanceof FloatTag) return ((FloatTag) tag).getAsFloat();
        if (tag instanceof LongTag) return ((LongTag) tag).getAsLong();
        if (tag instanceof ShortTag) return ((ShortTag) tag).getAsShort();
        if (tag instanceof ByteTag) return ((ByteTag) tag).getAsByte();
        if (tag instanceof ListTag) return ((ListTag) tag).copy();
        if (tag instanceof CompoundTag) return ((CompoundTag) tag).copy();
        return null;
    }

    public static @Nullable Tag toTag(@Nullable Object value) {
        if (value instanceof String) return StringTag.valueOf((String) value);
        if (value instanceof Integer) return IntTag.valueOf((Integer) value);
        if (value instanceof Double) return DoubleTag.valueOf((Double) value);
        if (value instanceof Float) return FloatTag.valueOf((Float) value);
        if (value instanceof Long) return LongTag.valueOf((Long) value);
        if (value instanceof Short) return ShortTag.valueOf((Short) value);
        if (value instanceof Byte) return ByteTag.valueOf((Byte) value);
        if (value instanceof ListTag) return ((ListTag) value).copy();
        if (value instanceof CompoundTag) return ((CompoundTag) value).copy();
        return null;
    }
}
