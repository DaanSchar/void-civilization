package com.voidcivilization.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CivilizationCommandUtil {

    public static final String COMMAND_PREFIX = "civ";

    public static CompletableFuture<Suggestions> recommendCivilizations(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder suggestionsBuilder
    ) {
        var civManager = CivilizationManager.get(context.getSource().getLevel());
        Collection<String> kingdomList = civManager.getCivilizations().keySet();

        return SharedSuggestionProvider.suggest(kingdomList, suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> recommendBoolean(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder suggestionsBuilder
    ) {
        return SharedSuggestionProvider.suggest(List.of("true", "false"), suggestionsBuilder);
    }

}
