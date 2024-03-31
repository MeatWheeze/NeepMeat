package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.machine.live_machine.LiveMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TestLivingMachineBlock extends LivingMachineBlock implements MeatlibBlock
{
    private final String name;

    public TestLivingMachineBlock(String name, Settings settings)
    {
        super(settings);
        this.name = name;
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LiveMachines.TEST_LIVING_MACHINE_BE.instantiate(pos, state);
    }
}
