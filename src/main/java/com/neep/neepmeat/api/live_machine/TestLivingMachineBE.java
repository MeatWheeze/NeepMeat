package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.machine.live_machine.component.HopperComponent;
import com.neep.neepmeat.machine.live_machine.component.ItemOutputComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Items;
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

        if (hasComponents(LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT))
        {
            var crushers = getComponent(LivingMachineComponents.CRUSHER_SEGMENT);
            var itemOutputs = getComponent(LivingMachineComponents.ITEM_OUTPUT);

            if (world.getTime() % 10 == 0)
            {
                itemOutputs.iterator().next().get().setStack(0, Items.STONE.getDefaultStack());
            }

//            HopperBlockEntity hopperBlockEntity = hopper.get();
//            ChestBlockEntity chestBlockEntity = itemOutput.get();
//            ItemStack stack = hopperBlockEntity.getStack(0);
//            if (!stack.isEmpty() && chestBlockEntity.getStack(0).isEmpty())
//            {
//                chestBlockEntity.setStack(0, stack);
//                hopperBlockEntity.setStack(0, ItemStack.EMPTY);
//            }
        }
    }
}
