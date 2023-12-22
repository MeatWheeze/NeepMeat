package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BigLeverBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    // Client only
    public float leverDelta = 0;

    // Server only
    public int activeTicks = 40;
    public int tickCounter = 0;
    public boolean powered;

    public BigLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BigLeverBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.BIG_LEVER, pos, state);
    }

    public boolean isCounting()
    {
        return activeTicks > 0;
    }

    protected void count()
    {
        if (isCounting())
        {
            if (tickCounter > 0)
            {
                --tickCounter;
                this.sync();
            }
            else
            {
                setPower(false);
            }
        }
    }

    public void togglePower()
    {
        setPower(!powered);
    }

    public void setPower(boolean pow)
    {
//        System.out.println(tickCounter);
        if (pow == powered)
            return;

        // Set world blockstate
        ((BigLeverBlock) getCachedState().getBlock()).setPowered(getWorld(), getPos(), pow);

        // Start counter if necessary
        if (pow)
        {
            powered = true;
            if (isCounting())
                tickCounter = activeTicks;
        }
        else
            powered = false;
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState blockState, BigLeverBlockEntity be)
    {
        be.count();
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        this.tickCounter = tag.getInt("counter");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        tag.putInt("counter", tickCounter);
        return tag;
    }
}
