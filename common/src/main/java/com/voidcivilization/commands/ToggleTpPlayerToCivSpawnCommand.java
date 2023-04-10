package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ToggleTpPlayerToCivSpawnCommand {

    private static boolean enabled = false;

    /**
     * /civ spawnatciv <boolean>
     * Toggles auto civ assign on join
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("spawnatciv")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .executes(ToggleTpPlayerToCivSpawnCommand::showStatus)
                                .then(
                                        Commands.argument("boolean", StringArgumentType.string())
                                                .suggests(CivilizationCommandUtil::recommendBoolean)
                                                .executes(ToggleTpPlayerToCivSpawnCommand::toggleAutoAssign)
                                )
                        )
        );
    }

    private static int toggleAutoAssign(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String bool = StringArgumentType.getString(context, "boolean");
        enabled = bool.equals("true");
        String message = "New players will now start at " +
                (enabled ? "their civilization's spawn point" : "the world spawn");

        Messenger.sendSuccess(player, message);
        return 0;
    }

    private static int showStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String message = "New players will start at " +
                (enabled ? "their civilization's spawn point" : "the world spawn");

        Messenger.sendMessage(player, "Players Start at Civ Spawn: " + enabled + ". " + message);
        return 0;
    }

    public static boolean isEnabled() {
        return enabled;
    }

}
