package com.neep.neepmeat.machine.advanced_integrator;

import com.google.common.collect.MapMaker;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.data.DataUtil;
import com.neep.neepmeat.client.sound.BlockSoundInstance;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class AdvancedIntegratorBlockEntity extends SyncableBlockEntity
{
    private final DataStorage storage = new DataStorage(this::sync);

    public AdvancedIntegratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void onUse(PlayerEntity player)
    {
        player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".integrator.data",
                DataUtil.formatData(storage.getAmount()),
                DataUtil.formatData(storage.getCapacity())), true);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        storage.writeNbt(nbt);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        storage.readNbt(nbt);
        super.readNbt(nbt);
    }

    public DataStorage getDataStorage()
    {
        return storage;
    }

    public void serverTick()
    {
        // Passive accumulation. Exceeding this limit requires a Pylon.
        if (storage.getAmount() < 8 * DataUtil.GIEB && world.getTime() % 4 == 0)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                storage.insert(DataVariant.NORMAL, 4, transaction);
                transaction.commit();
            }
        }
    }

    public void clientTick()
    {
        if (client == null)
            client = new Client(this);
        client.tick();
//        Client.get(this).tick();
    }

    public static class DataStorage extends SingleVariantStorage<DataVariant>
    {
        private final Runnable finalCallback;

        public DataStorage(Runnable finalCallback)
        {
            this.finalCallback = finalCallback;
        }

        protected void setAmount(long amount)
        {
            this.amount = amount;
        }

        @Override
        protected DataVariant getBlankVariant()
        {
            return DataVariant.BLANK;
        }

        @Override
        protected long getCapacity(DataVariant variant)
        {
            return DataUtil.GIEB * 16;
        }

        @Override
        public long insert(DataVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

            if ((insertedVariant.equals(variant) || variant.isBlank()) && canInsert(insertedVariant)) {
                long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - amount);

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);

                    if (variant.isBlank()) {
                        variant = insertedVariant;
                        amount = insertedAmount;
                    } else {
                        amount += insertedAmount;
                    }

                    return insertedAmount;
                }
            }

            return 0;
        }

        public void readNbt(NbtCompound nbt)
        {
            this.variant = DataVariant.fromNbt(nbt.getCompound("variant"));
            this.amount = nbt.getLong("amount");
        }

        @Override
        protected void onFinalCommit()
        {
            super.onFinalCommit();
            finalCallback.run();
        }
    }

    @Environment(EnvType.CLIENT)
    private Client client;

    @Environment(EnvType.CLIENT)
    private static class Client
    {
        private final MinecraftClient client = MinecraftClient.getInstance();
        private final BlockSoundInstance sound;

        public Client(AdvancedIntegratorBlockEntity be)
        {
            this.sound = new BlockSoundInstance(NMSounds.ADVANCED_INTEGRATOR_AMBIENT, SoundCategory.BLOCKS, be.getPos().up(3));
        }

        public void tick()
        {
            if (!client.getSoundManager().isPlaying(sound))
            {
                client.getSoundManager().play(sound);
            }
        }
    }
}
