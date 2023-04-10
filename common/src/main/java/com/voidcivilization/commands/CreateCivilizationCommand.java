package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class CreateCivilizationCommand {

    /**
     * /civ add <name>
     * Creates a new civilization with the given name
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("add")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                .executes(CreateCivilizationCommand::createNewCivilization)
                        ))
        );
    }

    private static int createNewCivilization(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");

        var civManager = CivilizationManager.get(player.level);
        if (civManager.getCivilization(civName).isPresent()) {
            Messenger.sendError(player, "There already exists a civilization with the name " + civName);
            return 0;
        }

        civManager.createCivilization(civName);

        if (civManager.getCivilization(civName).isPresent()) {
            Messenger.sendSuccess(player, "Successfully created a new civilization with the name " + civName);
            Messenger.sendMessageToEveryoneExcept(player, "Civilization " + civName + " has been created");
            civManager.syncClientCivilizationData(player.getLevel());
        } else {
            Messenger.sendError(player, "Failed to create a new civilization with the name " + civName);
        }

        return 0;
    }

}
