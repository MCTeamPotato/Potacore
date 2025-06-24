package com.teampotato.potacore.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods for manipulating enchantments on items and enchanted books.
 * <p>
 * This class supports removing specific enchantments from both regular items and enchanted books.
 * It handles both {@link Enchantment} instances and enchantments identified by {@link ResourceLocation}.
 */
public class EnchantHelper {

    /**
     * Removes multiple enchantments from an item stack.
     *
     * @param stack         The item stack to modify
     * @param enchantments  Iterable collection of enchantments to remove
     * @return Number of enchantments successfully removed from the item
     *
     * @see #removeEnchantment(ItemStack, Enchantment)
     */
    public static int removeEnchantments(ItemStack stack, @NotNull Iterable<Enchantment> enchantments) {
        int count = 0;
        for (Enchantment enchantment : enchantments) {
            if (EnchantHelper.removeEnchantment(stack, enchantment)) count++;
        }
        return count;
    }

    /**
     * Removes a specific enchantment from an item stack using its registry name.
     *
     * @param stack        The item stack to modify
     * @param enchantment  Registry name (ResourceLocation) of the enchantment to remove
     * @return {@code true} if the enchantment was found and removed, {@code false} otherwise
     *
     * @see #removeEnchantment(ItemStack, Enchantment)
     */
    public static boolean removeEnchantment(ItemStack stack, ResourceLocation enchantment) {
        return EnchantHelper.removeEnchantment(stack, ForgeRegistries.ENCHANTMENTS.getValue(enchantment));
    }

    /**
     * Removes a specific enchantment from an item stack or enchanted book.
     * <p>
     * Handles both regular items (using "Enchantments" tag) and enchanted books
     * (using "StoredEnchantments" tag). If the resulting NBT becomes empty after removal,
     * the entire NBT tag is cleared.
     *
     * @param stack        The item stack to modify. Must not be empty.
     * @param enchantment  Enchantment to remove. Must not be {@code null}.
     * @return {@code true} if the enchantment was found and removed, {@code false} otherwise
     */
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
