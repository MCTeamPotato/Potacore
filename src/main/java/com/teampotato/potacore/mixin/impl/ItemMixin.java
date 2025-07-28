package com.teampotato.potacore.mixin.impl;

import com.teampotato.potacore.api.RegistryNameContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public abstract class ItemMixin implements RegistryNameContainer {
    @Unique private ResourceLocation potacore$registryName = null;

    @Override
    public ResourceLocation getRegistryName() {
        if (potacore$registryName == null) potacore$registryName = ForgeRegistries.ITEMS.getKey((Item) (Object) this);
        return potacore$registryName;
    }
}
