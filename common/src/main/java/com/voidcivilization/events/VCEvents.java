package com.voidcivilization.events;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;

public class VCEvents {

    public static void register() {
        PlayerRespawnEvent.register();
        PlaceBlockEvent.register();
        BreakBlockEvent.register();
        PlayerDeathEvent.register();
        JoinServerEvent.register();
        LivingHurtEvent.register();
        ServerTickEvent.register();
        PlayerChatEvent.register();
    }
}
