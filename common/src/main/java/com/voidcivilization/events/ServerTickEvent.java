package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.commands.SetForceFieldRangeCommand;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.data.death.DeathTracker;
import com.voidcivilization.util.Messenger;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;


public class ServerTickEvent {

    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(level -> {
            if (!level.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            if (VoidCivilization.config.playerDeathCooldown > 0) {
                handleDeathTracking(level);
            }

        });
        TickEvent.PLAYER_POST.register(player -> {
            if (player.level.isClientSide) {
                return;
            }
            if (!player.level.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            if (SetForceFieldRangeCommand.getRange() > 0) {
                enforceForceField((ServerPlayer) player);
            }

            boostPlayersInBase((ServerPlayer) player);
        });
    }

    private static void handleDeathTracking(ServerLevel level) {
        DeathTracker deathTracker = DeathTracker.get(level);
        deathTracker.update();

        deathTracker.getDeathTimers().forEach((uuid, remainingTicks) -> {
            var player = level.getPlayerByUUID(uuid);

            if (player instanceof ServerPlayer serverPlayer) {
                if (remainingTicks % 20 == 0) {
                    serverPlayer.setGameMode(GameType.SPECTATOR);
                    Messenger.sendClientError(serverPlayer, "You will be revived in " + (remainingTicks / 20) + " seconds.");
                    CivilizationManager.get(level).updatePlayerDisplayName(serverPlayer);
                }
            }
        });

        deathTracker.getPlayersToBeRevived().forEach(uuid -> {
            var player = level.getPlayerByUUID(uuid);

            if (player instanceof ServerPlayer serverPlayer) {
                respawnPlayer(serverPlayer);
            }
        });
    }

    private static void respawnPlayer(ServerPlayer player) {
        DeathTracker deathTracker = DeathTracker.get(player.level);
        Messenger.sendClientSuccess(player, "You have been revived!");
        deathTracker.removePlayerDeath(player.getUUID());

        final int ticksPerSecond = 20;
        BlockPos spawn = getPlayerSpawn(player);
        player.teleportTo(spawn.getX(), spawn.getY(), spawn.getZ());
        player.setGameMode(GameType.SURVIVAL);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * ticksPerSecond, 99));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10 * ticksPerSecond, 1));
    }

    private static BlockPos getPlayerSpawn(ServerPlayer player) {
        var civManager = CivilizationManager.get(player.level);
        var civ = civManager.getCivilization(player.getGameProfile());

        if (civ.isPresent()) {
            var civSpawn = civ.get().getSpawn();
            if (civSpawn.isPresent()) {
                return civSpawn.get();
            }
        }

        return player.level.getSharedSpawnPos();
    }

    private static void enforceForceField(ServerPlayer player) {
        if (player.isCreative() || (player.isSpectator() && player.hasPermissions(3))) {
            return;
        }

        var civ = CivilizationManager.get(player.level).getCivilization(player.getGameProfile());

        if (civ.isEmpty()) {
            return;
        }

        var nucleus = civ.get().getNucleus();

        if (nucleus.isEmpty()) {
            return;
        }

        BlockPos nucleusPos = nucleus.get();
        BlockPos playerPos = player.blockPosition();

        final int range = SetForceFieldRangeCommand.getRange();
        double distanceToNucleus = getFlatDistance(nucleusPos, playerPos);

        if (distanceToNucleus < range) {
            return;
        }

        var spawn = civ.get().getSpawn();

        if (spawn.isEmpty()) {
            return;
        }

        BlockPos spawnPos = spawn.get();

        player.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        Messenger.sendClientError(player, "You are not allowed to wander that far away yet!");
    }

    private static double getFlatDistance(BlockPos from, BlockPos to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }

    private static void boostPlayersInBase(ServerPlayer player) {
        var civManager  = CivilizationManager.get(player.level);
        var civ = civManager.getCivilization(player.getGameProfile());

        if (civ.isEmpty()) {
            return;
        }

        var nucleus = civ.get().getNucleus();

        if (nucleus.isEmpty()) {
            return;
        }

        var nucleusInRange = civManager.getNucleusInRange(player.blockPosition(), VoidCivilization.config.nucleusProtectionRadius);

        if (nucleusInRange.isEmpty()) {
            return;
        }

        if (!nucleusInRange.get().equals(nucleus.get())) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, 0));
    }

}
