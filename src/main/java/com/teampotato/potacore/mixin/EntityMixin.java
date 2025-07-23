package com.teampotato.potacore.mixin;

import com.teampotato.potacore.data.EntitiesInChunkData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private Level level;

    @Shadow protected UUID uuid;

    @Shadow private BlockPos blockPosition;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;createInsecureUUID(Lnet/minecraft/util/RandomSource;)Ljava/util/UUID;"), require = 0)
    private UUID initId(RandomSource randomSource) {
        UUID id = Mth.createInsecureUUID(randomSource);
        if (this.level instanceof ServerLevel serverLevel) {
            while (serverLevel.getEntity(id) != null) {
                id = Mth.createInsecureUUID(randomSource);
            }
        }
        return id;
    }

    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ChunkPos;<init>(Lnet/minecraft/core/BlockPos;)V"))
    private void onUpdateChunkPos(double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof LivingEntity entity && entity.level() instanceof ServerLevel serverLevel) {
            EntitiesInChunkData.removeEntity(entity, serverLevel);
            EntitiesInChunkData.entities
                    .computeIfAbsent(this.level.dimension().location(), key -> EntitiesInChunkData.map())
                    .computeIfAbsent(new ChunkPos(this.blockPosition), key -> EntitiesInChunkData.set())
                    .add(this.uuid);
        }
    }
}
