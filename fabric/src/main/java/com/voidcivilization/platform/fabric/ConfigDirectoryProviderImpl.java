package com.voidcivilization.platform.fabric;

import java.nio.file.Path;

public class ConfigDirectoryProviderImpl {
    public static Path getConfigDirectory() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
    }
}
