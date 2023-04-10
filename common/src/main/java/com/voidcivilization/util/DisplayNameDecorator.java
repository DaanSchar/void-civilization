package com.voidcivilization.util;

import com.voidcivilization.client.ClientCivilizationData;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.data.death.DeathTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public class DisplayNameDecorator {

    /**
     * Returns the player's decorated name.
     * works for both client and server
     * @param player
     * @return
     */
    public static Component getPlayerDisplayName(Player player, boolean showDeathTimer) {
        var civ = getCivilization(player);
        MutableComponent styledCivComponent;
        if (civ.isPresent()) {
            styledCivComponent = getCivComponent(civ.get());
        } else if (player.hasPermissions(3)) {
            styledCivComponent = Component.literal("[Admin]").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
        } else {
            return player.getName();
        }

        MutableComponent displayName = Component.literal("").append(styledCivComponent).append(" ").append(player.getName());

        // death timer data only lives server side
        if (player instanceof ServerPlayer serverPlayer  && showDeathTimer) {
            DeathTracker deathTracker = DeathTracker.get(player.getLevel());
            UUID playerId = player.getGameProfile().getId();

            if (deathTracker.isPlayerDead(playerId)) {
                displayName = displayName.append(" ").append(getDeathTimerComponent(serverPlayer));
            }
        }

        return displayName;
    }



    private static Optional<Civilization> getCivilization(Player player) {
        if (player.level.isClientSide) {
            return ClientCivilizationData.getCivilization(player.getGameProfile());
        }

        return CivilizationManager.get(player.level).getCivilization(player.getGameProfile());
    }

    private static MutableComponent getCivComponent(Civilization civ) {
        return Component.literal("[" + civ.getName() + "]").withStyle(civ.getColor());
    }

    private static MutableComponent getDeathTimerComponent(ServerPlayer player) {
        DeathTracker deathTracker = DeathTracker.get(player.getLevel());
        int deathTimer = deathTracker.getDeathTimer(player.getUUID());
        final int ticksPerSecond = 20;
        return Component.literal("(" + (deathTimer / ticksPerSecond) + ")").withStyle(ChatFormatting.GRAY);
    }

    private DisplayNameDecorator() {
    }

}
