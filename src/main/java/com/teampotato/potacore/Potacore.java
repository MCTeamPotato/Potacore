package com.teampotato.potacore;

import com.teampotato.potacore.data.EntitiesInChunkData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Potacore.MOD_ID)
public class Potacore {
    public static final String MOD_ID = "potacore";
    public static final Logger LOGGER = LogManager.getLogger(Potacore.class);

    public Potacore() {
        EntitiesInChunkData.register();
        MinecraftForge.EVENT_BUS.addListener(EntitiesInChunkData.Debug::onTick);
    }
}
