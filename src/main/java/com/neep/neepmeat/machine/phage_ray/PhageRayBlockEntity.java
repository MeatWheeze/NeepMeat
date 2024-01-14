package com.neep.neepmeat.machine.phage_ray;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhageRayBlockEntity extends SyncableBlockEntity
{
    @Nullable private PhageRayEntity tetheredEntity;

    public PhageRayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        if (tetheredEntity == null)
        {
            Box box = new Box(pos.up());
            List<PhageRayEntity> entities = world.getEntitiesByClass(PhageRayEntity.class, box, e -> true);

            if (entities.size() > 1)
            {
                NeepMeat.LOGGER.warn("Phage Ray at {} {} {} seems to have too many tethered entities!", pos.getX(), pos.getY(), pos.getZ());
            }

            if (entities.isEmpty())
            {
                tetheredEntity = NMEntities.PHAGE_RAY.create(world);
                tetheredEntity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(tetheredEntity);
            }
            else
            {
                tetheredEntity = entities.get(0);
            }
        }

        Box box = new Box(pos.up());
        List<PhageRayEntity> entities = world.getEntitiesByClass(PhageRayEntity.class, box, e -> true);

        if (entities.size() > 1)
        {
            NeepMeat.LOGGER.warn("Phage Ray at {} {} {} seems to have too many tethered entities!", pos.getX(), pos.getY(), pos.getZ());
            for (int i = 1; i < entities.size(); ++i)
            {
                entities.get(i).remove(Entity.RemovalReason.DISCARDED);
            }
        }

        tetheredEntity.setParent(this);
    }
}
