package com.voidcivilization.registry.blocks.custom.block;

import com.voidcivilization.registry.blocks.VCBlockEntities;
import com.voidcivilization.registry.blocks.custom.entity.NucleusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class NucleusBlock extends BaseEntityBlock implements EntityBlock {

    public NucleusBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new NucleusBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, VCBlockEntities.NUCLEUS_BLOCK_ENTITY.get(), NucleusBlockEntity::tick);
    }
}
