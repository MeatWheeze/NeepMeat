package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.EncasedBlockEntity;
import com.neep.neepmeat.transport.block.energy_transport.EncasedVascularConduitBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class EncasedConduitBlockEntity extends VascularConduitBlockEntity implements EncasedBlockEntity
{
    private BlockState camoState = Blocks.AIR.getDefaultState();
    private VoxelShape cachedShape = null;

    public EncasedConduitBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.ENCASED_VASCULAR_CONDUIT, pos, state);
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
    protected void writeNbt(NbtCompound nbt)
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
                if (getCachedState().getBlock() instanceof EncasedVascularConduitBlock block)
                {
                    cachedShape = block.getPipeOutlineShape(getCachedState(), world, getPos());
                }

                VoxelShape camoShape = camoState.getOutlineShape(world, pos);
                cachedShape = VoxelShapes.union(camoShape, cachedShape);
            }
        }
        return cachedShape;
    }
}
