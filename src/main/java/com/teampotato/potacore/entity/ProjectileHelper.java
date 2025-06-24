package com.teampotato.potacore.entity;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for handling projectile-related entity operations with deduplication logic.
 * <p>
 * This class provides a method to process two potentially related entities while ensuring
 * the same entity isn't processed twice when both references point to the same entity.
 * </p>
 */
public class ProjectileHelper {

    /**
     * Processes the specified entities with the given action, avoiding duplicate processing
     * when both parameters refer to the same entity.
     * @param entity        The primary entity to process (typically a projectile). May be null.
     * @param directEntity  The direct source entity to process (typically the projectile owner). May be null.
     * @param action        The non-null runnable action to perform.
     *
     * @throws NullPointerException if {@code action} is null
     */
    public static void handle(@Nullable Entity entity, @Nullable Entity directEntity, @NotNull Runnable action) {
        if (entity != null) action.run();
        if (entity != null && directEntity != null && entity.getUUID().equals(directEntity.getUUID())) return;
        if (directEntity != null) action.run();
    }
}