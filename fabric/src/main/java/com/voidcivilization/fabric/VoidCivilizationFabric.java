package com.voidcivilization.fabric;

import com.voidcivilization.VoidCivilization;
import net.fabricmc.api.ModInitializer;

public class VoidCivilizationFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VoidCivilization.init();
    }
}
