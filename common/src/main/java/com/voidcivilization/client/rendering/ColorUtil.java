package com.voidcivilization.client.rendering;

import net.minecraft.ChatFormatting;

public class ColorUtil {

    public static int toHex(ChatFormatting formatting) {
        if (formatting == ChatFormatting.BLACK) {
            return 0x000000;
        }
        if (formatting == ChatFormatting.DARK_BLUE) {
            return 0x0000AA;
        }
        if (formatting == ChatFormatting.DARK_GREEN) {
            return 0x00AA00;
        }
        if (formatting == ChatFormatting.DARK_AQUA) {
            return 0x00AAAA;
        }
        if (formatting == ChatFormatting.DARK_RED) {
            return 0xAA0000;
        }
        if (formatting == ChatFormatting.DARK_PURPLE) {
            return 0xAA00AA;
        }
        if (formatting == ChatFormatting.GOLD) {
            return 0xFFAA00;
        }
        if (formatting == ChatFormatting.GRAY) {
            return 0xAAAAAA;
        }
        if (formatting == ChatFormatting.DARK_GRAY) {
            return 0x555555;
        }
        if (formatting == ChatFormatting.BLUE) {
            return 0x5555FF;
        }
        if (formatting == ChatFormatting.GREEN) {
            return 0x55FF55;
        }
        if (formatting == ChatFormatting.AQUA) {
            return 0x55FFFF;
        }
        if (formatting == ChatFormatting.RED) {
            return 0xFF5555;
        }
        if (formatting == ChatFormatting.LIGHT_PURPLE) {
            return 0xFF55FF;
        }
        if (formatting == ChatFormatting.YELLOW) {
            return 0xFFFF55;
        }

        return 0xFFFFFF;
    }

}
