package com.neep.neepmeat.block.entity;

import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.machine.advanced_integrator.SimpleDataPort;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorStructureBlockEntity extends BigBlockStructureBlockEntity
{
    @Nullable private AdvancedIntegratorBlockEntity parent;

    private final SimpleDataPort port = new SimpleDataPort(this)
    {
        @Override
        public long receive(DataVariant variant, long amount, TransactionContext transaction)
        {
            var parent = getParent();
            if (parent != null)
            {
                return parent.getDataStorage().insert(variant, amount, transaction);
            }
            return 0;
        }
    };

    public AdvancedIntegratorStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public DataPort getPort(Void unused)
    {
        return port;
    }

    public AdvancedIntegratorBlockEntity getParent()
    {
        if (parent == null)
        {
            parent = (AdvancedIntegratorBlockEntity) world.getBlockEntity(getControllerPos());
        }
        return parent;
    }
}
