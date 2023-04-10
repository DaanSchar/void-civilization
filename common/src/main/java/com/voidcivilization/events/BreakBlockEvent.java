package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.BreakNucleusPacket;
import com.voidcivilization.networking.packets.SyncCivilizationsDataPacket;
import com.voidcivilization.registry.blocks.VCBlocks;
import com.voidcivilization.util.Messenger;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BreakBlockEvent {

    public static void register() {
        BlockEvent.BREAK.register((level, blockPos, blockState, serverPlayer, intValue) -> {
            if (level.isClientSide()) {
                return EventResult.pass();
            }

            if (!blockState.getBlock().equals(VCBlocks.NUCLEUS_BLOCK.get())) {
                return handleNonNucleusBlock(blockPos, level, serverPlayer);
            }

            var civManager = CivilizationManager.get(level);
            var attackingCiv = civManager.getCivilization(serverPlayer.getGameProfile());

            if (attackingCiv.isEmpty()) {
                Messenger.sendClientError(serverPlayer, "You are not in a civilization");
                return EventResult.interruptFalse();
            }

            var attackedCiv = civManager.getCivilizationByNucleus(blockPos);

            if (attackedCiv.isEmpty()) {
                Messenger.sendClientError(serverPlayer, "This nucleus is not part of a civilization. Please contact an admin");
                return EventResult.interruptFalse();
            }

            boolean playerBreaksOwnNucleus = attackingCiv.equals(attackedCiv);
            boolean isAdmin = serverPlayer.hasPermissions(3) && serverPlayer.isCreative();

            if (playerBreaksOwnNucleus) {
                if (isAdmin) {
                    attackingCiv.get().setNucleus(null);
                    civManager.setDirty();
                    Messenger.sendClientSuccess(serverPlayer, "Removed nucleus from " + attackingCiv.get().getName());
                    return EventResult.pass();
                } else {
                    Messenger.sendClientError(serverPlayer, "You can't break your own nucleus");
                    return EventResult.interruptFalse();
                }
            }

            if (isAdmin) {
                Messenger.sendClientError(serverPlayer, "You can't remove other civilizations' nucleus");
                return EventResult.interruptFalse();
            }

            attackedCiv.get().damage(VoidCivilization.config.damagePerNucleusBreak);
            civManager.setDirty();

            NetworkHandler.sendToAllPlayers(serverPlayer.getLevel(), new BreakNucleusPacket(attackedCiv.get(), attackingCiv.get()));
            NetworkHandler.sendToAllPlayers(serverPlayer.getLevel(), new SyncCivilizationsDataPacket(serverPlayer.getLevel()));
            serverPlayer.getLevel().playSound(null, blockPos, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (attackedCiv.get().isDead()) {
                attackedCiv.get().setNucleus(null);
                attackedCiv.get()
                        .getMembers((ServerLevel) level)
                        .forEach(LivingEntity::kill);
                civManager.setDirty();
                return EventResult.pass();
            }

            return EventResult.interruptFalse();
        });
    }

    private static EventResult handleNonNucleusBlock(BlockPos pos, Level level, ServerPlayer serverPlayer) {
        if (serverPlayer.hasPermissions(3) && serverPlayer.isCreative()) {
            return EventResult.pass();
        }

        var civManager = CivilizationManager.get(level);
        var nucleusInRange = civManager.getNucleusInRange(pos, VoidCivilization.config.nucleusProtectionRadius);

        if (nucleusInRange.isEmpty()) {
            return EventResult.pass();
        }

        return EventResult.interruptFalse();
    }

}
