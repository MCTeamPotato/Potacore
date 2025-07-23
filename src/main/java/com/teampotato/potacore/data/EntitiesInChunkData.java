package com.teampotato.potacore.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jol.info.ClassLayout;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EntitiesInChunkData {
    public static final Map<ResourceLocation, Map<ChunkPos, Set<UUID>>> entities = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public static Set<UUID> getEntitiesInChunk(@NotNull Level level, ChunkPos pos) {
        return entities.getOrDefault(level.dimension().location(), Collections.emptyMap()).getOrDefault(pos, Collections.emptySet());
    }

    public static @NotNull Map<ChunkPos, Set<UUID>> map() {
        return Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    }

    public static @NotNull Set<UUID> set() {
        return ObjectSets.synchronize(new ObjectOpenHashSet<>());
    }

    public static void removeEntity(@NotNull LivingEntity entity, @NotNull ServerLevel level) {
        ChunkPos chunkPos = entity.chunkPosition();
        ResourceLocation dim = level.dimension().location();

        Map<ChunkPos, Set<UUID>> entitiesInChunk = entities.getOrDefault(dim, null);
        if (entitiesInChunk == null) return;

        Set<UUID> entitySet = entitiesInChunk.getOrDefault(chunkPos, null);
        if (entitySet == null) return;

        entitySet.remove(entity.getUUID());
        if (!entitySet.isEmpty()) return;

        entitiesInChunk.remove(chunkPos);
        if (!entitiesInChunk.isEmpty()) return;

        entities.remove(dim);
    }

    public static void register() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EntitiesInChunkData::onChunkUnLoad);
        bus.addListener(EntitiesInChunkData::onLevelUnLoad);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onJoin);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onLeave);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onDie);
        bus.addListener(EventPriority.LOWEST, EntitiesInChunkData::onTravel);
        bus.addListener(EntitiesInChunkData::onShutdown);
    }

    private static void onChunkUnLoad(ChunkEvent.@NotNull Unload event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            Map<ChunkPos, Set<UUID>> entitiesInChunk = entities.getOrDefault(level.dimension().location(), null);
            if (entitiesInChunk == null) return;
            entitiesInChunk.remove(chunk.getPos());
        }
    }

    private static void onLevelUnLoad(LevelEvent.@NotNull Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            entities.remove(level.dimension().location());
        }
    }

    private static void onJoin(@NotNull EntityJoinLevelEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel level && level.isLoaded(entity.blockPosition())) {
            entities
                    .computeIfAbsent(level.dimension().location(), key -> map())
                    .computeIfAbsent(entity.chunkPosition(), key -> set())
                    .add(entity.getUUID());
        }
    }

    private static void onLeave(@NotNull EntityLeaveLevelEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    private static void onDie(@NotNull LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    private static void onTravel(@NotNull EntityTravelToDimensionEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    private static void onShutdown(ServerStoppingEvent event) {
        entities.clear();
    }

    public static final class Debug {
        private static int tick = 1200;

        public static void onTick(TickEvent.LevelTickEvent event) {
            if (event.side != LogicalSide.SERVER && event.phase != TickEvent.Phase.END) return;
            tick--;
            if (tick <= 0) {
                tick = 1200;
                System.out.println(ClassLayout.parseInstance(entities).toPrintable());
            }
        }
    }
}