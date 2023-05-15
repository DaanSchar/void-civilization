package com.voidcivilization.fabric;

import com.voidcivilization.client.rendering.ForceFieldRenderer;
import com.voidcivilization.client.rendering.hud.CivilizationScoreboard;
import com.voidcivilization.client.rendering.hud.KDAScoreboard;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class VoidCivilizationClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(CivilizationScoreboard::render);
        HudRenderCallback.EVENT.register(KDAScoreboard::render);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> ForceFieldRenderer.render(
                context.matrixStack(),
                context.tickDelta())
        );
    }
}
