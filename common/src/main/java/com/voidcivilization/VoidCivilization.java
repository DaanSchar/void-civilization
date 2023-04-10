package com.voidcivilization;

import com.voidcivilization.commands.VCCommands;
import com.voidcivilization.config.ServerConfig;
import com.voidcivilization.config.ServerConfigWrapper;
import com.voidcivilization.events.VCEvents;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.registry.blocks.VCBlockEntities;
import com.voidcivilization.registry.blocks.VCBlocks;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoidCivilization {

    public static final Logger LOGGER = LoggerFactory.getLogger(VoidCivilization.class);
    public static final String MOD_ID = "voidcivilization";
    public static ServerConfig config;

    public static void init() {
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        VCBlocks.registerBlocks();
        VCBlockEntities.registerBlockEntities();
        VCCommands.register();
        VCEvents.register();
        NetworkHandler.register();
    }
}
