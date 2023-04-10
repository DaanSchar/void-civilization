package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.VoidCivilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class SetCivilizationHealthCommand {

    /**
     * /civ sethealth <name> <amount>
     * Sets the color of the civilization
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("sethealth")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(CivilizationCommandUtil::recommendCivilizations)
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(SetCivilizationHealthCommand::setHealth)
                                        )
                                )
                        )
        );
    }

    private static int setHealth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");
        int health = IntegerArgumentType.getInteger(context, "amount");
        var civManager = CivilizationManager.get(player.level);
        var civ = civManager.getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " does not exist");
            return 1;
        }

        if (health < 0) {
            Messenger.sendError(player, "Health cannot be negative");
            return 0;
        }

        final int maxHealth = VoidCivilization.config.maxNucleusHealth;

        if (health > maxHealth) {
            Messenger.sendError(player, "Health cannot be greater than " + maxHealth);
            return 0;
        }
        civ.get().setHealth(health);
        civManager.setDirty();

        civManager.syncClientCivilizationData(player.getLevel());

        Messenger.sendSuccess(player, "Set health of " + civName + " to " + health);

        return 0;
    }
}
