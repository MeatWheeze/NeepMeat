package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TableControllerBlockEntity extends BloodMachineBlockEntity
{
//    private Int2ObjectMap<BlockApiCache<Storage<?>, Direction>> caches = new Int2ObjectArrayMap<>();
    private List<BlockApiCache<?, Direction>> caches = new ArrayList<>(9);

    public TableControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TableControllerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TABLE_CONTROLLER, pos, state);
    }

    public void assemble()
    {
        caches.clear();

        Direction facing = getCachedState().get(BaseHorFacingBlock.FACING).getOpposite();
        Direction left = facing.rotateYCounterclockwise();
        BlockPos corner = pos.offset(facing).offset(left).up();

        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int j = 0; j < 3; ++j)
        {
            for (int i = 0; i < 3; ++i)
            {
                Vec3i xVec = left.getOpposite().getVector().multiply(i);
                Vec3i zVec = facing.getVector().multiply(j);
                mutable.set(corner, xVec);
                mutable.set(mutable, zVec);
                caches.add(createCache((ServerWorld) world, mutable));
                ((ServerWorld) world).spawnParticles(ParticleTypes.COMPOSTER, mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5, 5, 0, 0, 0, 0);
            }
        }
    }

    private static BlockApiCache<?, Direction> createCache(ServerWorld world, BlockPos pos)
    {
        return BlockApiCache.create(FluidStorage.SIDED, world, pos);
    }
}
