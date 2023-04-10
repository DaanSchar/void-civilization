package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.VoidCivilization;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.UpdateConfigPacket;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class SetForceFieldRangeCommand {

    private static int range = 0;

    /**
     * /civ forcefield <integer>
     * Sets the range of the force field
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("forcefield")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .executes(SetForceFieldRangeCommand::showStatus)
                                .then(
                                        Commands.argument("number", IntegerArgumentType.integer(0, 1000))
                                                .executes(SetForceFieldRangeCommand::set)
                                )
                        )
        );
    }

    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        range = IntegerArgumentType.getInteger(context, "number");

        Messenger.sendSuccess(player, "Force field range set to " + range + " blocks");
        NetworkHandler.sendToAllPlayers(
                player.getLevel(),
                new UpdateConfigPacket(
                        VoidCivilization.config.nucleusProtectionRadius,
                        SetForceFieldRangeCommand.getRange(),
                        VoidCivilization.config.maxNucleusHealth
                )
        );
        return 0;
    }

    private static int showStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Messenger.sendMessage(player, "Force field range is currently set to " + range + " blocks");
        return 0;
    }

    public static int getRange() {
        return range;
    }

}
