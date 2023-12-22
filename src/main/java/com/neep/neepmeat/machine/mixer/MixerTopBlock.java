package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.block.IMeatBlock;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MixerTopBlock extends Block implements IMeatBlock, BlockEntityProvider
{
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, -16, 0, 16, 16, 16);
    private final String registryName;

    public MixerTopBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings.nonOpaque().solidBlock(InventoryDetectorBlock::never));
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OUTLINE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MixerTopBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockState(pos.down()).isOf(NMBlocks.MIXER))
        {
            if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity be)
            {
                be.dropItems();
            }
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return NMBlocks.MIXER.getPickStack(world, pos, state);
    }

    @Override
    public ItemConvertible dropsLike()
    {
        return NMBlocks.MIXER;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        return 1;
    }

    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder)
    {
        Identifier identifier = NMBlocks.MIXER.getLootTableId();
        if (identifier == LootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContext.getWorld();
        LootTable lootTable = serverWorld.getServer().getLootManager().getTable(identifier);
        return lootTable.generateLoot(lootContext);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }
}
