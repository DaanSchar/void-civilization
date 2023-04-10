package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class StatsCommand {


    /**
     * /civ stats
     * Displays the stats of the civilization the player is in
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("stats")
                                .requires(sourceStack -> sourceStack.hasPermission(0))
                                .executes(StatsCommand::listCivilizations))
        );
    }

    private static int listCivilizations(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var civManager = CivilizationManager.get(player.level);
        var civ = civManager.getCivilization(player.getGameProfile());

        if (civ.isEmpty()) {
            Messenger.sendError(player, "You are not in a civilization");
            return 0;
        }

        Messenger.sendMessage(player,
                "Civilization: " + civ.get().getName() +
                        "\nMembers: " + civ.get().getMembers().size() +
                        "\nHealth: " + civ.get().getHealth()
        );

        return 0;
    }

}
