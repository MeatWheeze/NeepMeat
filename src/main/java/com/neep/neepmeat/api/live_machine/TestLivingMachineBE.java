package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
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

        withComponents(LivingMachineComponents.LARGEST_HOPPER, LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var hoppers = result.t1();
            var crushers = result.t2();
            var itemOutputs = result.t3();
            var motors = result.t4();

            float power = 0;
            for (var motor : motors)
            {
                power = Math.max(power, motor.getPower());
            }

            if (power < 0.1)
            {
                power = 0;
                return;
            }

            float progressIncrement = power / crushers.size() * 4;

            InventoryStorage input = hoppers.iterator().next().getStorage(null);
            Storage<ItemVariant> output = itemOutputs.iterator().next().getStorage(null);

            try (Transaction transaction = Transaction.openOuter())
            {
                for (var crusher : crushers)
                {
                    CrusherSegmentBlockEntity.InputSlot slot = crusher.getStorage();

                    if (slot.isEmpty())
                        StorageUtil.move(input, slot, v -> true, 1, transaction);

                    if (!slot.isEmpty())
                    {
                        slot.tick(progressIncrement, output, transaction);
                    }
                }
                transaction.commit();
            }
        });
    }

    public static class SimpleCrushingStorage implements IGrinderStorage
    {
        private final Storage<ItemVariant> input;
        private final Storage<ItemVariant> output;
        private XpStorage xpStorage = new XpStorage();

        public SimpleCrushingStorage(Storage<ItemVariant> input, Storage<ItemVariant> output)
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
