package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class TankBlockEntity extends BlockEntity implements FluidBufferProvider
{
    private final FluidBuffer buffer;

    public TankBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new FluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    public TankBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.TANK_BLOCK_ENTITY, pos, state);
        this.buffer = new FluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNBT(tag);
    }


    @Override
    @Nullable
    public FluidBuffer getBuffer(Direction direction)
    {
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {

    }
}
