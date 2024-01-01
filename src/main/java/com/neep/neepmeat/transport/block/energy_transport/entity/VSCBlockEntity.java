package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.block.energy_transport.VSCBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VSCBlockEntity extends BlockEntity
{
    protected long influx;

    protected final BloodAcceptor backAcceptor = new BloodAcceptor()
    {
        @Override
        public Mode getMode()
        {
            return Mode.ACTIVE_SINK;
        }

        @Override
        public float updateInflux(float influx)
        {
            if (influx >= 0.1)
            {
                VSCBlockEntity.this.influx = (long) (0.1 * PowerUtils.referencePower());
                return 0.1f;
            }
            else
            {
                VSCBlockEntity.this.influx = 0;
                return 0;
            }
        }
    };

    protected final BloodAcceptor frontAcceptor = new BloodAcceptor()
    {
        @Override
        public Mode getMode()
        {
            return Mode.SOURCE;
        }

        @Override
        public long getOutput()
        {
            return influx;
        }
    };

    public VSCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BloodAcceptor getBloodAcceptor(Direction face)
    {
        if (getCachedState().get(VSCBlock.FACING) == face.getOpposite())
        {
            return backAcceptor;
        }
        return frontAcceptor;
    }
}