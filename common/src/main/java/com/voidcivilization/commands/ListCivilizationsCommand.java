package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class ListCivilizationsCommand {

    /**
     * /civ list
     * Displays a list of all civilizations
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("list").executes(ListCivilizationsCommand::listCivilizations))
        );
    }

    private static int listCivilizations(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Map<String, Civilization> civMap = CivilizationManager.get(player.level).getCivilizations();

        if (civMap.isEmpty()) {
            Messenger.sendMessage(player, "There are no civilizations yet");
            return 0;
        }

        String civNamesList = civMap.values()
                .stream()
                .map(Civilization::getName)
                .collect(java.util.stream.Collectors.joining(", "));

        Messenger.sendMessage(player, civNamesList);

        return 0;
    }
}
