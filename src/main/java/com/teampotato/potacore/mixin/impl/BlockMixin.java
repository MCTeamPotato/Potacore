package com.teampotato.potacore.mixin.impl;

import com.teampotato.potacore.api.RegistryNameContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class BlockMixin implements RegistryNameContainer {
    @Unique
    private ResourceLocation potacore$registryName = null;

    @Override
    public ResourceLocation getRegistryName() {
        if (potacore$registryName == null) potacore$registryName = ForgeRegistries.BLOCKS.getKey((Block) (Object) this);
        return potacore$registryName;
    }
}
