package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PumpBlockEntity extends BlockEntity
{
    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);
    }

    private int number = 7;

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity be)
    {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("number", number);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        number = tag.getInt("number");
    }
}
