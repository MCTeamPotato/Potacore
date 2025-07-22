package com.teampotato.potacore.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ItemEnchantEvent extends Event {
    private final ItemStack itemStack;
    private Enchantment enchantment;
    private int level;

    public ItemEnchantEvent(ItemStack itemStack, Enchantment enchantment, int level) {
        this.itemStack = itemStack;
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getLevel() {
        return this.level;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String toString() {
        return "EnchantedBook{" + "itemStack=" + this.itemStack + ", enchantment=" + getEnchantment().getDescriptionId() + ", level=" + getLevel() + '}';
    }

    @Cancelable
    public static final class EnchantedBook extends Event {
        private final ItemStack itemStack;
        private EnchantmentInstance enchantmentInstance;

        public EnchantedBook(ItemStack itemStack, EnchantmentInstance enchantmentInstance) {
            this.enchantmentInstance = enchantmentInstance;
            this.itemStack = itemStack;
        }

        public EnchantmentInstance getEnchantmentInstance() {
            return this.enchantmentInstance;
        }

        public Enchantment getEnchantment() {
            return this.getEnchantmentInstance().enchantment;
        }

        public int getLevel() {
            return this.getEnchantmentInstance().level;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public void setEnchantment(Enchantment enchantment) {
            this.setEnchantmentInstance(enchantment, this.getLevel());
        }

        public void setLevel(int level) {
            this.setEnchantmentInstance(this.getEnchantment(), level);
        }

        public void setEnchantmentInstance(Enchantment enchantment, int level) {
            this.setEnchantmentInstance(new EnchantmentInstance(enchantment, level));
        }

        public void setEnchantmentInstance(EnchantmentInstance enchantmentInstance) {
            this.enchantmentInstance = enchantmentInstance;
        }

        public String toString() {
            return "EnchantedBook{" + "itemStack=" + this.itemStack + ", enchantment=" + getEnchantment().getDescriptionId() + ", level=" + getLevel() + '}';
        }
    }
}
