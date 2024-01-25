package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Maps;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface VascularConduitEntity extends RememberMyNetwork
{
    BlockApiLookup<VascularConduitEntity, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "vascular_conduit_entity"),
            VascularConduitEntity.class, Void.class);

    Map<BlockEntityType<?>, Function<? extends BlockEntity, VascularConduitEntity>> MAP = Maps.newHashMap();

    static <T extends BlockEntity> void registerPersistentNetwork(Function<T, VascularConduitEntity> function, BlockEntityType<T> type)
    {
        MAP.put(type, function);
    }

    @Nullable
    static <T extends BlockEntity> VascularConduitEntity find(T blockEntity)
    {
        var function = MAP.get(blockEntity.getType());
        if (function != null)
        {
            // This may be a little naughty
            Function<T, VascularConduitEntity> casted = (Function<T, VascularConduitEntity>) function;
            return casted.apply(blockEntity);
        }
        return null;
    }

    static VascularConduitEntity find(World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof VascularConduitEntity entity)
            return entity;

        return LOOKUP.find(world, pos, null);
    }

    @Nullable
    BloodNetwork getNetwork();

    void setNetwork(@Nullable BloodNetwork network);

    default BlockPos getBlockPos()
    {
        if (this instanceof BlockEntity be)
            return be.getPos();

        throw new NotImplementedException();
    }

    @Override
    default VascularConduitEntity get() { return this; }

    enum UpdateReason
    {
        ADDED,
        REMOVED,
        CHANGED
    }
}
