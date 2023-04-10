package com.voidcivilization.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class ConfigDirectoryProvider {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }
}
