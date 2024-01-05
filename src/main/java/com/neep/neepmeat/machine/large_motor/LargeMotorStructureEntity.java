package com.neep.neepmeat.machine.large_motor;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LargeMotorStructureEntity extends BigBlockStructureEntity
{
    private final AcceptorWrapper acceptorWrapper;
    @Nullable private BlockApiCache<Void, Void> controllerCache;

    public LargeMotorStructureEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.acceptorWrapper = new AcceptorWrapper();
    }

    public BloodAcceptor getBloodAcceptor(Direction direction)
    {
        return acceptorWrapper;
    }

    @Nullable
    private LargeMotorBlockEntity getController()
    {
        if (controllerCache == null)
        {
            if (controllerPos != null && world instanceof ServerWorld serverWorld)
            {
                controllerCache = BlockApiCache.create(MeatLib.VOID_LOOKUP, serverWorld, controllerPos);
            }
            else
            {
                return null;
            }
        }

        if (controllerCache.getBlockEntity() instanceof LargeMotorBlockEntity be)
        {
            return be;
        }

        return null;
    }

    private class AcceptorWrapper implements BloodAcceptor
    {
        @Override
        public Mode getMode()
        {
            return Mode.SINK;
        }

        @Override
        public float updateInflux(float influx)
        {
            var controller = getController();
            if (getController() != null)
            {
                return controller.getAcceptor(null).updateInflux(influx);
            }
            return 0;
        }
    }
}
