package com.neep.neepmeat.api.live_machine;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.EnumMap;

public class TestLivingMachineBE extends LivingMachineBlockEntity
{
    public TestLivingMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    protected void processStructure()
    {
        EnumMap<LivingMachineStructure.Property, AtomicDouble> map = new EnumMap<>(LivingMachineStructure.Property.class);
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, value) ->
            {
                map.computeIfAbsent(property, p -> new AtomicDouble(0)).addAndGet(value);
            });
        }
    }
}
