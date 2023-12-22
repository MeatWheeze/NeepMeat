package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class GlassTankBlockEntity extends TankBlockEntity
{
//    private final WritableFluidBuffer buffer;

    public GlassTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.GLASS_TANK, pos, state);
//        this.buffer = new WritableFluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
    }
}
