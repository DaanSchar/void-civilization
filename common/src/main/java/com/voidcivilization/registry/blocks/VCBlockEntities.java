package com.voidcivilization.registry.blocks;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.platform.RegistryHelper;
import com.voidcivilization.registry.blocks.custom.entity.NucleusBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class VCBlockEntities {

    public static final Supplier<BlockEntityType<NucleusBlockEntity>> NUCLEUS_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("nucleus_block_entity", () -> BlockEntityType.Builder.of(NucleusBlockEntity::new, VCBlocks.NUCLEUS_BLOCK.get()).build(null));

    public static void registerBlockEntities() {
        VoidCivilization.LOGGER.info("Block entities have been registered");
    }

}
