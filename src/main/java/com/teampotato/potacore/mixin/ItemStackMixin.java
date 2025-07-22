package com.teampotato.potacore.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.teampotato.potacore.event.ItemEnchantEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @WrapMethod(method = "enchant")
    private void onEnchant(Enchantment enchantment, int level, Operation<Void> original) {
        ItemEnchantEvent event = new ItemEnchantEvent((ItemStack) (Object) this, enchantment, level, false);
        boolean cancel = MinecraftForge.EVENT_BUS.post(event);
        if (!cancel) original.call(event.getEnchantment(), event.getLevel());
    }
}
