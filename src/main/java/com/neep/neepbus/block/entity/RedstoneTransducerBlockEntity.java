package com.neep.neepbus.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepbus.block.RedstoneTransducerBlock;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepbus.util.SimpleEntry;
import com.neep.neepbus.util.WritePort;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RedstoneTransducerBlockEntity extends SyncableBlockEntity implements ConfigProvider
{
    private final WritePort writePort = this::receive;

    private int outputLevel;

    public long lastRedstoneUpdate;

    private final NeepBusConfig config = NeepBusConfig.builder(this::markDirty)
            .input(new SimpleEntry("Redstone"), writePort)
            .applyChanges(this)
            .build();

    public RedstoneTransducerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    private void receive(int data)
    {
        this.outputLevel = MathHelper.clamp(data, 0, 15);

        long time = getWorld().getTime();

        Block block = getCachedState().getBlock();

        int remainder = (int) (time % 4);
        if (!getWorld().getBlockTickScheduler().isQueued(pos, block))
        {
            if (remainder == 0)
            {
                ((RedstoneTransducerBlock) getCachedState().getBlock()).updateRedstone(getWorld(), pos, this);
            }
            else
            {
                getWorld().scheduleBlockTick(pos, block, 4 - remainder);
            }
        }
    }

    public int getValue()
    {
        return outputLevel;
    }

    @Override
    public NeepBusConfig getConfig()
    {
        return config;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        config.writeNbt(nbt);
        nbt.putInt("output_level", outputLevel);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        config.readNbt(nbt);
        this.outputLevel = nbt.getInt("output_level");
    }
}
