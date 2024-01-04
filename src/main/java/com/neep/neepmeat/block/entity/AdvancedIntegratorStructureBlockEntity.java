package com.neep.neepmeat.block.entity;

import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorBlockEntity;
import com.neep.neepmeat.machine.advanced_integrator.SimpleDataPort;
import com.neep.neepmeat.machine.integrator.Integrator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorStructureBlockEntity extends BigBlockStructureEntity implements Integrator
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

    @Override
    public BlockPos getBlockPos() { return getPos(); }

    @Override
    public boolean canEnlighten() { return true; }

    @Override
    public void setLookPos(BlockPos pos)
    {

    }

    @Override
    public void spawnBeam(World world, BlockPos pos)
    {
        Integrator.spawnBeam((ServerWorld) world, getControllerPos().up(4), pos);
        world.playSound(null, pos, NMSounds.ADVANCED_INTEGRATOR_CHARGE, SoundCategory.BLOCKS, 20, 0.8f);
    }

    @Override
    public long getData(DataVariant variant)
    {
        return getParent().getDataStorage().getAmount();
    }

    @Override
    public float extract(DataVariant variant, long amount, TransactionContext transaction)
    {
        return getParent().getDataStorage().extract(variant, amount, transaction);
    }
}
