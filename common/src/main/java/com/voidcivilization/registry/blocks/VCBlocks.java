package com.voidcivilization.registry.blocks;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.platform.RegistryHelper;
import com.voidcivilization.registry.VCCreativeModeTab;
import com.voidcivilization.registry.blocks.custom.block.NucleusBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VCBlocks {

    public static final Map<String, Supplier<Block>> BLOCKS = new HashMap<>();

    public static final Supplier<NucleusBlock> NUCLEUS_BLOCK = registerBlock("nucleus_block", true, () -> new NucleusBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).explosionResistance(36000f).lightLevel((state) -> 15)));

    @SuppressWarnings("unchecked")
    private static <T extends Block> Supplier<T> registerBlock(String name, boolean createItem, Supplier<T> block) {
        Supplier<T> toReturn = RegistryHelper.registerBlock(name, block);
        BLOCKS.put(name, (Supplier<Block>) toReturn);
        if (createItem) RegistryHelper.registerItem(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(VCCreativeModeTab.DD_TAB)));
        return toReturn;
    }

    public static void registerBlocks() {
        VoidCivilization.LOGGER.info("Void Civilization blocks have been registered");
    }

}
