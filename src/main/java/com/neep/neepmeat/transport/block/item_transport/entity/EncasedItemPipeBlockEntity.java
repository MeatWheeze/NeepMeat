package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.transport.block.EncasedBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.EncasedItemPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class EncasedItemPipeBlockEntity extends ItemPipeBlockEntity implements EncasedBlockEntity
{
    private BlockState camoState = Blocks.AIR.getDefaultState();
    private VoxelShape cachedShape = null;

    public EncasedItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        var nbt = super.toInitialChunkDataNbt();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.camoState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("camo_state"));
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("camo_state", NbtHelper.fromBlockState(camoState));
    }

    @Override
    public BlockState getCamoState()
    {
        return camoState;
    }

    @Override
    public void setCamoState(BlockState camoState)
    {
        this.camoState = camoState;
        cachedShape = null;
        markDirty();
    }

    public void onNeighbourUpdate()
    {
        cachedShape = null;
    }

    @Override
    public VoxelShape getCamoShape()
    {
        if (cachedShape == null)
        {
            cachedShape = VoxelShapes.fullCube();
            if (getCamoState().isOf(getCachedState().getBlock()) || getCamoState().isAir())
            {
                return cachedShape;
            }
            else
            {
                if (getCachedState().getBlock() instanceof EncasedItemPipeBlock block)
                {
                    cachedShape = block.getPipeOutlineShape(getCachedState(), world, getPos());
                }

                VoxelShape camoShape = camoState.getOutlineShape(world, pos);
                cachedShape = VoxelShapes.union(camoShape, cachedShape);
            }
        }
        return cachedShape;
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        readNbt(nbt);
    }

    @Override
    public void toClientTag(NbtCompound nbt)
    {
        writeNbt(nbt);
    }

    @Override
    public void onReceiveNbt(NbtCompound nbt)
    {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
    }
}
