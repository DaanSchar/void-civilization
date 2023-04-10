package com.voidcivilization.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.util.Messenger;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SetCivilizationColorCommand {

    /**
     * /civ setcolor <name> <color>
     * Sets the color of the civilization
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("setcolor")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(CivilizationCommandUtil::recommendCivilizations)
                                        .then(Commands.argument("color", StringArgumentType.string())
                                                .suggests(SetCivilizationColorCommand::recommendColors)
                                                .executes(SetCivilizationColorCommand::setColor)
                                        )
                                )
                        )
        );
    }

    private static int setColor(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");
        String colorName = StringArgumentType.getString(context, "color");
        ChatFormatting color = ChatFormatting.getByName(colorName.toUpperCase());
        var civManager = CivilizationManager.get(player.level);
        var civ = civManager.getCivilization(civName);

        if (color == null || !color.isColor()) {
            Messenger.sendError(player, colorName + " is not a color");
            return 0;
        }

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " does not exist");
            return 1;
        }
        civ.get().setColor(color);
        civManager.setDirty();
        civ.get().getMembers()
                .stream()
                .map(member -> (ServerPlayer) player.getLevel().getPlayerByUUID(member.getId()))
                .filter(Objects::nonNull)
                .forEach(civManager::updatePlayerDisplayName);

        civManager.syncClientCivilizationData(player.getLevel());

        Messenger.sendSuccess(player, "Set color of " + civName + " to " + colorName);

        return 0;
    }

    private static CompletableFuture<Suggestions> recommendColors(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder suggestionsBuilder
    ) {
        var formats = Arrays.stream(ChatFormatting.values()).toList();
        var formatNames = formats.stream()
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .collect(Collectors.toList());

        return SharedSuggestionProvider.suggest(formatNames, suggestionsBuilder);
    }
}
