package com.voidcivilization.commands;

import com.voidcivilization.commands.swap.AcceptSwapRequestCommand;
import com.voidcivilization.commands.swap.MakeSwapRequestCommand;
import com.voidcivilization.commands.swap.ToggleSwapRequestsCommand;
import dev.architectury.event.events.common.CommandRegistrationEvent;

public class VCCommands {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            CreateCivilizationCommand.register(dispatcher);
            ListCivilizationsCommand.register(dispatcher);
            RemoveCivilizationCommand.register(dispatcher);
            MemberCommands.register(dispatcher);
            SetCivilizationSpawnCommand.register(dispatcher);
            TeleportToSpawnCommand.register(dispatcher);
            StatsCommand.register(dispatcher);
            ToggleTpPlayerToCivSpawnCommand.register(dispatcher);
            MakeSwapRequestCommand.register(dispatcher);
            AcceptSwapRequestCommand.register(dispatcher);
            ToggleSwapRequestsCommand.register(dispatcher);
            SetCivilizationColorCommand.register(dispatcher);
            SetForceFieldRangeCommand.register(dispatcher);
            SetCivilizationHealthCommand.register(dispatcher);
            WhiteListCommand.register(dispatcher);
        });
    }

}
