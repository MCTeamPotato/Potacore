package com.teampotato.potacore.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private Level level;

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
}
