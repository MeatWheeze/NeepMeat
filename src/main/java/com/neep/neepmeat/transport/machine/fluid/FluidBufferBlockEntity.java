package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FluidBufferBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    protected final WritableSingleFluidStorage buffer;

    public FluidBufferBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new WritableSingleFluidStorage((FluidConstants.BUCKET / 3), this::sync);
    }

    public FluidBufferBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLUID_BUFFER, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
    }

    @Override
    @Nullable
    public WritableSingleFluidStorage getBuffer(Direction direction)
    {
        return buffer;
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (WritableFluidBuffer.handleInteract(buffer, world, player, hand))
        {
            return true;
        }
        else if (!world.isClient())
        {
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, pos, buffer);
            return true;
        }
        return true;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return tag;
    }
}
