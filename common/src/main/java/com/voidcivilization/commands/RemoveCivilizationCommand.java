package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;
import java.util.Objects;

public class RemoveCivilizationCommand {

    /**
     * /civ remove <name>
     * Deletes the civilization with the given name
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("remove")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(CivilizationCommandUtil::recommendCivilizations)
                                        .executes(RemoveCivilizationCommand::removeCivilization)
                                ))
        );
    }

    private static int removeCivilization(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");

        var civManager = CivilizationManager.get(player.getLevel());
        var civ = civManager.getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " does not exist");
            return 0;
        }

        boolean removed = civManager.removeCivilization(civName);

        if (!removed) {
            Messenger.sendError(player, "Failed to remove " + civName);
            return 0;
        }

        var players = civ.get()
                .getMembers()
                .stream()
                .map(p -> (ServerPlayer) player.getLevel().getPlayerByUUID(p.getId()))
                .filter(Objects::nonNull)
                .toList();

        players.forEach(civManager::updatePlayerDisplayName);

        Messenger.sendSuccess(player, "Removed " + civName);

        civManager.updatePlayerDisplayName(player);
        civManager.syncClientCivilizationData(player.getLevel());

        return 0;
    }


}
