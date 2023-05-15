package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.commands.SetForceFieldRangeCommand;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.commands.ToggleTpPlayerToCivSpawnCommand;
import com.voidcivilization.data.player.PlayerManager;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.UpdateConfigPacket;
import com.voidcivilization.networking.packets.UpdateKDAPacket;
import com.voidcivilization.util.Messenger;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Stack;

public class JoinServerEvent {

    private static final Stack<Civilization> civilizations = new Stack<>();

    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            trackPlayerJoin(player);
            NetworkHandler.sendToPlayer(
                    new UpdateConfigPacket(
                            VoidCivilization.config.nucleusProtectionRadius,
                            SetForceFieldRangeCommand.getRange(),
                            VoidCivilization.config.maxNucleusHealth
                    ),
                    player
            );
            var civManager = CivilizationManager.get(player.getLevel());
            civManager.syncClientCivilizationData(player.getLevel());
            NetworkHandler.sendToAllPlayers(player.getLevel(), new UpdateKDAPacket(player.getLevel()));

            if (!VoidCivilization.config.autoAssignPlayersToCivilization) {
                return;
            }

            var playerManager = PlayerManager.get(player.getLevel());
            if (playerManager.isFirstJoin(player.getUUID())) {
                handleFirstJoin(player);
            }
        });
    }

    private static void trackPlayerJoin(Player player) {
        var playerManager = PlayerManager.get(player.getLevel());
        playerManager.join(player.getUUID());
    }

    private static void handleFirstJoin(ServerPlayer player) {
        var civManager = CivilizationManager.get(player.getLevel());
        if (civManager.isInCivilization(player.getGameProfile()) || civManager.getCivilizations().isEmpty()) {
            return;
        }

        Civilization assignedCiv = assignCivilization(player);
        assignedCiv.addMember(player.getGameProfile());
        civManager.setDirty();
        Messenger.sendMessage(player, "You've been assigned to " + assignedCiv.getName() + "!");
        civManager.updatePlayerDisplayName(player);
        civManager.syncClientCivilizationData(player.getLevel());

        if (ToggleTpPlayerToCivSpawnCommand.isEnabled()) {
            assignedCiv.getSpawn().ifPresent(spawn -> player.teleportTo(spawn.getX(), spawn.getY(), spawn.getZ()));
        }
    }

    private static Civilization assignCivilization(Player player) {
        String strategy = VoidCivilization.config.assignStrategy;

        if (strategy.equals("round-robin")) {
            return getCivRoundRobin(player);
        }

        return getCivRandom(player);
    }

    private static Civilization getCivRoundRobin(Player player) {
        var civManager = CivilizationManager.get(player.getLevel());

        if (civilizations.isEmpty()) {
            civManager.getCivilizations().values().forEach(civilizations::push);
        }

        return civilizations.pop();
    }

    private static Civilization getCivRandom(Player player) {
        var civManager = CivilizationManager.get(player.getLevel());
        var civs = civManager.getCivilizations().values();
        var civsArray = civs.toArray(new Civilization[0]);
        var randomIndex = player.getRandom().nextInt(civsArray.length);
        return civsArray[randomIndex];
    }

}
