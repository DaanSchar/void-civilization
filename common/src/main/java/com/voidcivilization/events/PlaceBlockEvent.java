package com.voidcivilization.events;

import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.registry.blocks.VCBlocks;
import com.voidcivilization.util.Messenger;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class PlaceBlockEvent {

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public static void register() {
        BlockEvent.PLACE.register((level, pos, state, entity) -> {
            if (level.isClientSide()) {
                return EventResult.pass();
            }

            if (entity instanceof ServerPlayer player) {
                if (!state.getBlock().equals(VCBlocks.NUCLEUS_BLOCK.get())) {
                    return handleNonNucleusBlock(pos, level, player);
                }

                var civManager = CivilizationManager.get(level);
                var civ = civManager.getCivilization(player.getGameProfile());

                if (!player.hasPermissions(3)) {
                    Messenger.sendClientError(player, "You do not have permission to place a that");
                    level.setBlock(pos, AIR, 3);
                    return EventResult.interruptFalse();
                }

                if (civ.isEmpty()) {
                    Messenger.sendClientError(player, "You are not in a civilization");
                    level.setBlock(pos, AIR, 3);
                    return EventResult.interruptFalse();
                }

                String civName = civ.get().getName();

                if (civ.get().getNucleus().isPresent()) {
                    Messenger.sendClientError(player, civName + " already has a nucleus. Remove it first");
                    level.setBlock(pos, AIR, 3);
                    return EventResult.interruptFalse();
                }

                civ.get().setNucleus(pos);
                civManager.setDirty();
                Messenger.sendClientSuccess(player, "Nucleus added to " + civName);
                civManager.syncClientCivilizationData(player.getLevel());
            }

            return EventResult.pass();
        });
    }

    private static EventResult handleNonNucleusBlock(BlockPos pos, Level level, ServerPlayer serverPlayer) {
        if (serverPlayer.hasPermissions(3) && serverPlayer.isCreative()) {
            return EventResult.pass();
        }

        var civManager = CivilizationManager.get(level);
        var nucleusInRange = civManager.getNucleusInRange(pos, 13);

        if (nucleusInRange.isEmpty()) {
            return EventResult.pass();
        }

        level.setBlock(pos, AIR, 3);
        return EventResult.interruptFalse();
    }

}
