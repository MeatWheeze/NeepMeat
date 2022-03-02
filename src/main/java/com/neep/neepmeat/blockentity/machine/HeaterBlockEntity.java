package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.mixin.FurnaceMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlockEntity extends BloodMachineBlockEntity<HeaterBlockEntity>
{
    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.HEATER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeaterBlockEntity blockEntity)
    {
//        System.out.println("iei");
        BlockPos offset = pos.offset(state.get(HeaterBlock.FACING));
        if (world.getBlockEntity(offset) instanceof AbstractFurnaceBlockEntity furnace)
        {
            ((FurnaceMixin) furnace).setBurnTime(50);
        }
    }
}
