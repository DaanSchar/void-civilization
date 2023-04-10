package com.voidcivilization.platform.forge;

import java.nio.file.Path;

public class ConfigDirectoryProviderImpl {
    public static Path getConfigDirectory() {
        return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
    }
}
