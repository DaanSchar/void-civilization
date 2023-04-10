package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class TeleportToSpawnCommand {

    /**
     * /civ tpall
     * Teleports all players to their respective civilization's spawn
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("tpall")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .executes(TeleportToSpawnCommand::teleportToSpawn))
        );
    }

    private static int teleportToSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        Player player = context.getSource().getPlayerOrException();
        Collection<Civilization> civs = CivilizationManager.get(level).getCivilizations().values();

        Messenger.sendMessage(player, "Teleporting...");

        for (Civilization civilization : civs) {
            if (civilization.getSpawn().isEmpty()) {
                Messenger.sendError(
                        player,
                        civilization.getName() +
                                " has no spawn set. Not teleporting members."
                );
            }

            civilization.teleportMembersToSpawn(level);
        }

        return 0;
    }

}
