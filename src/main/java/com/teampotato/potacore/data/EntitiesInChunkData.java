package com.teampotato.potacore.data;

import com.teampotato.potacore.Potacore;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntitiesInChunkData {
    public static final Map<ResourceLocation, Map<ChunkPos, Set<UUID>>> ENTITIES = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public static @NotNull Iterator<Entity> getEntitiesInChunk(@NotNull ServerLevel level, ChunkPos pos) {
        Set<UUID> entitySet = ENTITIES.getOrDefault(level.dimension().location(), Collections.emptyMap()).getOrDefault(pos, Collections.emptySet());

        Iterator<UUID> backingIterator = entitySet.iterator();

        return new Iterator<>() {
            private Entity nextValid = null;
            private boolean hasNextEvaluated = false;

            private void findNextValid() {
                while (backingIterator.hasNext()) {
                    UUID candidateId = backingIterator.next();
                    Entity candidate = level.getEntity(candidateId);
                    if (candidate != null) {
                        nextValid = candidate;
                        return;
                    } else {
                        backingIterator.remove();
                    }
                }
                nextValid = null;
            }

            @Override
            public boolean hasNext() {
                if (!hasNextEvaluated) {
                    findNextValid();
                    hasNextEvaluated = true;
                }
                return nextValid != null;
            }

            @Override
            public Entity next() {
                if (!hasNext()) throw new NoSuchElementException();
                hasNextEvaluated = false;
                return nextValid;
            }
        };
    }

    @ApiStatus.Internal
    public static @NotNull Map<ChunkPos, Set<UUID>> map() {
        return Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    }

    @ApiStatus.Internal
    public static @NotNull Set<UUID> set() {
        return ObjectSets.synchronize(new ObjectOpenHashSet<>());
    }

    @ApiStatus.Internal
    public static void removeEntity(@NotNull Entity entity, @NotNull ServerLevel level) {
        ChunkPos chunkPos = entity.chunkPosition();
        ResourceLocation dim = level.dimension().location();

        Map<ChunkPos, Set<UUID>> entitiesInChunk = ENTITIES.get(dim);
        if (entitiesInChunk == null) return;

        Set<UUID> entitySet = entitiesInChunk.get(chunkPos);
        if (entitySet == null) return;
        entitySet.remove(entity.getUUID());

        if (!entitySet.isEmpty()) return;

        entitiesInChunk.remove(chunkPos);
        if (!entitiesInChunk.isEmpty()) return;

        ENTITIES.remove(dim);
        Potacore.LOGGER.debug("Removed {} at {} in {}", entity.getName(), entity.position(), dim);
    }

    @ApiStatus.Internal
    public static void addEntity(@NotNull ServerLevel level, @NotNull Entity entity) {
        if (level.isLoaded(entity.blockPosition()) && entity.isAlive()) {
            ChunkPos pos = entity.chunkPosition();
            ResourceLocation dim = level.dimension().location();
            ENTITIES
                    .computeIfAbsent(dim, key -> map())
                    .computeIfAbsent(pos, key -> set())
                    .add(entity.getUUID());
            Potacore.LOGGER.debug("Add {} at {} in {}", entity.getName(), entity.position(), dim);
        }
    }

    public static void register() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EntitiesInChunkData::onChunkUnLoad);
        bus.addListener(EntitiesInChunkData::onLevelUnLoad);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onJoin);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onLeave);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onDie);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onTravel);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onDespawn);
        bus.addListener(EntitiesInChunkData::onShutdown);
    }

    private static void onChunkUnLoad(ChunkEvent.@NotNull Unload event) {
        if (event.getWorld() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            Map<ChunkPos, Set<UUID>> entitiesInChunk = ENTITIES.get(level.dimension().location());
            if (entitiesInChunk == null) return;
            entitiesInChunk.remove(chunk.getPos());
        }
    }

    private static void onLevelUnLoad(WorldEvent.@NotNull Unload event) {
        if (event.getWorld() instanceof ServerLevel level) {
            ENTITIES.remove(level.dimension().location());
        }
    }

    private static void onJoin(@NotNull EntityJoinWorldEvent event) {
        if (!event.isCanceled() && event.getEntity().level instanceof ServerLevel level) {
            addEntity(level, event.getEntity());
        }
    }

    private static void onLeave(@NotNull EntityLeaveWorldEvent event) {
        if (!event.isCanceled() && event.getEntity().level instanceof ServerLevel level) {
            removeEntity(event.getEntity(), level);
        }
    }

    private static void onDie(@NotNull LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.level instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    private static void onTravel(@NotNull EntityTravelToDimensionEvent event) {
        if (!event.isCanceled() && event.getEntity().level instanceof ServerLevel level) {
            removeEntity(event.getEntity(), level);
        }
    }

    private static void onDespawn(LivingSpawnEvent.@NotNull AllowDespawn event) {
        if (event.getResult().equals(Event.Result.DENY)) return;
        if (event.getEntity().level instanceof ServerLevel level) {
            removeEntity(event.getEntity(), level);
        }
    }

    private static void onShutdown(ServerStoppingEvent event) {
        ENTITIES.clear();
    }
}