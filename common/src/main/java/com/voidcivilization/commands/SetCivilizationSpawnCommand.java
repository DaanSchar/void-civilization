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

public class SetCivilizationSpawnCommand {

    /**
     * /civ setspawn <name>
     * Sets the spawn of the civilization to the player's current position
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("setspawn")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(CivilizationCommandUtil::recommendCivilizations)
                                .executes(SetCivilizationSpawnCommand::setSpawn)
                        ))
        );
    }

    private static int setSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");
        var civManager = CivilizationManager.get(context.getSource().getLevel());

        var civ = civManager.getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " does not exist");
            return 1;
        }

        civ.get().setSpawn(player.blockPosition());
        civManager.setDirty();
        Messenger.sendSuccess(player, "Spawn set for " + civName);

        return 0;
    }
}
