package com.voidcivilization.registry;

import com.voidcivilization.platform.RegistryHelper;
import com.voidcivilization.registry.blocks.VCBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VCCreativeModeTab {

    public static final CreativeModeTab DD_TAB = RegistryHelper.registerCreativeModeTab(() -> new ItemStack(VCBlocks.NUCLEUS_BLOCK.get().asItem()));


}
