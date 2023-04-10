package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.server.level.ServerPlayer;

public class LivingHurtEvent {

    public static void register() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (entity.getLevel().isClientSide) {
                return EventResult.pass();
            }

            if (entity instanceof ServerPlayer damageReceiver) {
                var civManager = CivilizationManager.get(damageReceiver.getLevel());

                if (source.getEntity() instanceof ServerPlayer damageSource) {
                    if (civManager.areInSameCiv(damageReceiver.getGameProfile(), damageSource.getGameProfile())) {
                        if (VoidCivilization.config.allowFriendlyFire) {
                            return EventResult.pass();
                        }

                        return EventResult.interruptFalse();
                    }
                }
            }
            return EventResult.pass();
        });
    }

}
