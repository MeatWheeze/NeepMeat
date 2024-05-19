package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.machine.grinder.CrusherRecipeContext;
import com.neep.neepmeat.screen_handler.LivingMachineScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TestLivingMachineBE extends LivingMachineBlockEntity implements ExtendedScreenHandlerFactory
{
    public TestLivingMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        super.serverTick();

//        withComponents(LivingMachineComponents.LARGEST_HOPPER, LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
//        {
//            var hoppers = result.t1();
//            var crushers = result.t2();
//            var itemOutputs = result.t3();
//
//            if (power < 0.1)
//            {
//                return;
//            }
//
//            float progressIncrement = getProgressIncrement() / crushers.size() * 4;
//
//            InventoryStorage input = hoppers.iterator().next().getStorage(null);
//            Storage<ItemVariant> output = itemOutputs.iterator().next().getStorage(null);
//
//            boolean hasInput = input.nonEmptyIterator().hasNext();
//            try (Transaction transaction = Transaction.openOuter())
//            {
//                for (var crusher : crushers)
//                {
//                    try (Transaction inner = transaction.openNested())
//                    {
//                        CrusherSegmentBlockEntity.InputSlot slot = crusher.getStorage();
//
//                        if (hasInput && slot.isEmpty())
//                            StorageUtil.move(input, slot, v -> true, 1, inner);
//
//                        if (!slot.isEmpty())
//                        {
//                            slot.tick(progressIncrement, output, inner);
//                        }
//                        inner.commit();
//                    }
//                }
//                transaction.commit();
//            }
//        });
    }

    @Override
    public Text getDisplayName()
    {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new LivingMachineScreenHandler(playerInventory, syncId, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }


    public static class SimpleCrushingStorage implements CrusherRecipeContext
    {
        private final Storage<ItemVariant> input;
        private final Storage<ItemVariant> output;
        private final XpStorage xpStorage = new XpStorage();
        private final float chanceMod;

        public SimpleCrushingStorage(Storage<ItemVariant> input, Storage<ItemVariant> output, float chanceMod)
        {
            this.input = input;
            this.output = output;
            this.chanceMod = chanceMod;
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

        @Override
        public float getChanceMod()
        {
            return chanceMod;
        }
    }
}
