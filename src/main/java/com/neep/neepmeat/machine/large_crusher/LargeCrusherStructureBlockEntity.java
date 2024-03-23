package com.neep.neepmeat.machine.large_crusher;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LargeCrusherStructureBlockEntity extends BigBlockStructureEntity
{
    @Nullable BlockApiCache<Void, Void> cache;

    public LargeCrusherStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MotorisedBlock getMotorised(Void unused)
    {
        if (apis.contains(MotorisedBlock.LOOKUP.getId()))
        {
            return getControllerBE();
        }
        return null;
    }

    @Nullable
    private LargeCrusherBlockEntity getControllerBE()
    {
        if (controllerPos == null)
        {
            return null;
        }
        else if (cache == null)
        {
            cache = BlockApiCache.create(MeatLib.VOID_LOOKUP, (ServerWorld) getWorld(), controllerPos);
        }

        return cache.getBlockEntity() instanceof LargeCrusherBlockEntity controller ? controller : null;
    }
}
