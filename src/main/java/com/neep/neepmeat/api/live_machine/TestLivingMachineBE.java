package com.neep.neepmeat.api.live_machine;

import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.recipe.GrindingRecipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
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

        if (hasComponents(LivingMachineComponents.HOPPER, LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT))
        {
            var hoppers = getComponent(LivingMachineComponents.HOPPER);
            var crushers = getComponent(LivingMachineComponents.CRUSHER_SEGMENT);
            var itemOutputs = getComponent(LivingMachineComponents.ITEM_OUTPUT);

            Storage<ItemVariant> input = hoppers.iterator().next().getStorage();
            Storage<ItemVariant> output = itemOutputs.iterator().next().getStorage(null);

            CrushingStorage context = new CrushingStorage(input, output);
            GrindingRecipe recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.GRINDING, context).orElse(null);
            if (recipe != null)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    recipe.takeInputs(context, transaction);
                    recipe.ejectOutputs(context, transaction);
                    transaction.commit();
                }
            }

//            if (world.getTime() % 10 == 0)
//            {
//                itemOutputs.iterator().next().get().setStack(0, Items.STONE.getDefaultStack());
//            }

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

    private static class CrushingStorage implements IGrinderStorage
    {
        private final Storage<ItemVariant> input;
        private final Storage<ItemVariant> output;
        private XpStorage xpStorage = new XpStorage();

        public CrushingStorage(Storage<ItemVariant> input, Storage<ItemVariant> output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public Storage<ItemVariant> getInputStorage()
        {
            return input;
        }

        @Override
        public Storage<ItemVariant> getOutputStorage()
        {
            return output;
        }

        @Override
        public Storage<ItemVariant> getExtraStorage()
        {
            return output;
        }

        @Override
        public XpStorage getXpStorage()
        {
            return xpStorage;
        }
    }
}
