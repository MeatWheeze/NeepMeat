package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixerTopBlockEntity extends SyncableBlockEntity
{
    public MixerTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MixerTopBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MIXER_TOP, pos, state);
    }

    public MixerBlockEntity getBottom()
    {
        if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity be)
        {
            return be;
        }
        return null;
    }

    public static Storage<FluidVariant> getBottomStorage(World world, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction)
    {
        if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity mixer)
        {
            return mixer.getOutputStorage();
        }
        return null;
    }
}
