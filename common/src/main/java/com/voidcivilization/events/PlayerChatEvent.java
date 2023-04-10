package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.util.DisplayNameDecorator;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatEvent {

    public static void register() {
        ChatEvent.RECEIVED.register((player, message) -> {
            if (!VoidCivilization.config.localizeChat) {
                return EventResult.pass();
            }

            if (player == null) {
                return EventResult.pass();
            }

            if (player.isSpectator()) {
                return EventResult.interruptFalse();
            }

            player.getLevel()
                    .players()
                    .forEach(p -> {
                        if (player.distanceTo(p) <= VoidCivilization.config.chatRadius) {
                            p.displayClientMessage(decorateMessage(player, message), false);
                        }
                    });

            return EventResult.interruptFalse();
        });
    }

    private static Component decorateMessage(ServerPlayer player, Component message) {
        Component playerName = DisplayNameDecorator.getPlayerDisplayName(player, false);
        Component separator = Component.literal(" > ");
        return Component.empty().append(playerName).append(separator).append(message);
    }

}
