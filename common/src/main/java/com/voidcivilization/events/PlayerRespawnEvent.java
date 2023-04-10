package com.voidcivilization.events;

import dev.architectury.event.events.common.PlayerEvent;

public class PlayerRespawnEvent {

    public static void register() {
        PlayerEvent.PLAYER_RESPAWN.register((player, isEnd) -> {
            player.getLastDeathLocation().ifPresent(location -> {
                player.teleportTo(location.pos().getX(), location.pos().getY(), location.pos().getZ());
            });
        });
    }

}
