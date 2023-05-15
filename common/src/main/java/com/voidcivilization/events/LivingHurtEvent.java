package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.data.kda.KDATracker;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.server.level.ServerPlayer;

public class LivingHurtEvent {

    public static void register() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (entity.getLevel().isClientSide) {
                return EventResult.pass();
            }

            if (entity instanceof ServerPlayer player) {
                if (source.getEntity() instanceof ServerPlayer attacker) {
                    return handlePlayerHitPlayer(player, attacker);
                }
            }
            return EventResult.pass();
        });
    }

    private static EventResult handlePlayerHitPlayer(ServerPlayer player, ServerPlayer attacker) {
        var civManager = CivilizationManager.get(player.getLevel());

        if (civManager.areInSameCiv(player.getGameProfile(), attacker.getGameProfile())) {
            if (VoidCivilization.config.allowFriendlyFire) {
                return EventResult.pass();
            }

            return EventResult.interruptFalse();
        }

        KDATracker kdaTracker = KDATracker.get(player.getLevel());
        kdaTracker.playerWasHit(
                player.getGameProfile().getId(),
                attacker.getGameProfile().getId()
        );

        return EventResult.pass();
    }

}
