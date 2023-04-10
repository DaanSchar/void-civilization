package com.voidcivilization.fabric;

import com.voidcivilization.client.HudRenderHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class VoidCivilizationClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(HudRenderHelper::render);
    }
}
