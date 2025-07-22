package com.teampotato.potacore.entity;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectileHelper {
    public static void handle(@Nullable Entity entity, @Nullable Entity directEntity, @NotNull Runnable action) {
        if (entity != null) action.run();
        if (entity != null && directEntity != null && entity.getUUID().equals(directEntity.getUUID())) return;
        if (directEntity != null) action.run();
    }
}