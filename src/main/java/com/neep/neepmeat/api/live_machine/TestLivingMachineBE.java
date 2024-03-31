package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.machine.live_machine.LiveMachines;
import com.neep.neepmeat.machine.live_machine.component.HopperComponent;
import com.neep.neepmeat.machine.live_machine.component.ItemOutputComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class TestLivingMachineBE extends LivingMachineBlockEntity
{

    public TestLivingMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        super.serverTick();

        if (hasComponents(LiveMachines.HOPPER, LiveMachines.ITEM_OUTPUT))
        {
            HopperComponent hopper = getComponent(LiveMachines.HOPPER);
            ItemOutputComponent itemOutput = getComponent(LiveMachines.ITEM_OUTPUT);

            HopperBlockEntity hopperBlockEntity = hopper.get();
            ChestBlockEntity chestBlockEntity = itemOutput.get();
            ItemStack stack = hopperBlockEntity.getStack(0);
            if (!stack.isEmpty() && chestBlockEntity.getStack(0).isEmpty())
            {
                chestBlockEntity.setStack(0, stack);
                hopperBlockEntity.setStack(0, ItemStack.EMPTY);
            }
        }
    }
}
