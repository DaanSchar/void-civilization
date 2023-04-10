package com.voidcivilization.config;

import com.voidcivilization.VoidCivilization;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = VoidCivilization.MOD_ID)
public class ServerConfigWrapper extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("Server")
    @ConfigEntry.Gui.Excluded
    public ServerConfig server = new ServerConfig();
}
