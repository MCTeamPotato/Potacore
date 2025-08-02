package com.teampotato.potacore.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class EnchantHelper {
    public static boolean hasEnchantment(@NotNull ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(enchantment);
    }

    public static boolean hasEnchantment(@NotNull ItemStack stack, ResourceLocation id) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(ForgeRegistries.ENCHANTMENTS.getValue(id));
    }

    public static int removeEnchantments(ItemStack stack, @NotNull Iterable<Enchantment> enchantments) {
        int count = 0;
        for (Enchantment enchantment : enchantments) {
            if (EnchantHelper.removeEnchantment(stack, enchantment)) count++;
        }
        return count;
    }

    public static boolean removeEnchantment(ItemStack stack, ResourceLocation enchantment) {
        return EnchantHelper.removeEnchantment(stack, ForgeRegistries.ENCHANTMENTS.getValue(enchantment));
    }

    public static boolean removeEnchantment(@NotNull ItemStack stack, Enchantment enchantment) {
        if (stack.isEmpty() || enchantment == null || !stack.hasTag()) return false;

        ResourceLocation targetId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (targetId == null) return false;

        String tagId = "Enchantments";
        if (stack.getItem().equals(Items.ENCHANTED_BOOK)) tagId = "StoredEnchantments";

        CompoundTag nbt = stack.getOrCreateTag();
        ListTag enchantmentList = nbt.getList(tagId, 10);

        ListTag newList = new ListTag();
        boolean modified = false;

        for (int i = 0; i < enchantmentList.size(); i++) {
            CompoundTag enchantmentTag = enchantmentList.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(enchantmentTag.getString("id"));

            if (id == null || !id.equals(targetId)) {
                newList.add(enchantmentTag);
            } else {
                modified = true;
            }
        }

        if (modified) {
            if (newList.isEmpty()) {
                nbt.remove(tagId);
            } else {
                nbt.put(tagId, newList);
            }

            if (nbt.isEmpty()) stack.setTag(null);
        }

        return modified;
    }
}
