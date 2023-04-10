package com.voidcivilization.forge;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.platform.forge.RegistryHelperImpl;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VoidCivilization.MOD_ID)
public class VoidCivilizationForge {
    public VoidCivilizationForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RegistryHelperImpl.BLOCKS.register(bus);
        RegistryHelperImpl.ITEMS.register(bus);
        RegistryHelperImpl.BLOCK_ENTITIES.register(bus);
        EventBuses.registerModEventBus(VoidCivilization.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        VoidCivilization.init();
    }
}
