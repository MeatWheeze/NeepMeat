package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassTankBlockEntity extends TankBlockEntity implements com.neep.neepmeat.fluid_transfer.FluidBuffer.FluidBufferProvider, BlockEntityClientSerializable
{
//    private final WritableFluidBuffer buffer;

    public GlassTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.GLASS_TANK_BLOCK_ENTITY, pos, state);
//        this.buffer = new WritableFluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        buffer.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return buffer.writeNbt(tag);
    }

    @Override
    public void sync()
    {
        World world = this.getWorld();
        if (world != null && !world.isClient)
        {
            BlockEntityClientSerializable.super.sync();
        }
    }
}
