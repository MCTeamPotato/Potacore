package com.teampotato.potacore.mixin.impl;

import com.teampotato.potacore.api.RegistryNameContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements RegistryNameContainer {
    @Unique private ResourceLocation potacore$registryName = null;

    @Override
    public ResourceLocation getRegistryName() {
        if (potacore$registryName == null) potacore$registryName = ForgeRegistries.ENTITIES.getKey((EntityType<?>) (Object) this);
        return potacore$registryName;
    }
}
