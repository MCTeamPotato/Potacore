package com.teampotato.potacore;

import com.teampotato.potacore.event.ItemEnchantEvent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("potacore")
public class Potacore {
    public static final Logger LOGGER = LogManager.getLogger(Potacore.class);

    public Potacore() {
        MinecraftForge.EVENT_BUS.addListener((ItemEnchantEvent event) -> {
            event.setEnchantment(Enchantments.UNBREAKING);
            event.setLevel(3);
        });
    }
}
