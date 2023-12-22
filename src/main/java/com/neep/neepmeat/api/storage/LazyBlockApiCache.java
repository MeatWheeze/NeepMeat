package com.neep.neepmeat.api.storage;

import com.neep.neepmeat.plc.instruction.Argument;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class LazyBlockApiCache<A, C>
{
    private BlockApiCache<A, C> cache = null;
    private final Supplier<ServerWorld> worldSupplier;
    private final BlockApiLookup<A, C> lookup;
    private final BlockPos pos;
    private final Supplier<C> ctxSupplier;

    private LazyBlockApiCache(Supplier<World> worldSupplier, BlockApiLookup<A, C> lookup, BlockPos pos, Supplier<C> ctxSupplier)
    {
        this.worldSupplier = () ->
        {
            if (worldSupplier.get() instanceof ServerWorld serverWorld)
            {
                return serverWorld;
            }
            throw new IllegalArgumentException("LazyBlockApiCache queried on the client!");
        };
        this.lookup = lookup;
        this.pos = pos;
        this.ctxSupplier = ctxSupplier;
    }

    public A find()
    {
        validate();
        return cache.find(ctxSupplier.get());
    }

    private void validate()
    {
        if (cache == null)
        {
            cache = BlockApiCache.create(lookup, worldSupplier.get(), pos);
        }
    }

    public static <A, C> LazyBlockApiCache<A, C> of(BlockApiLookup<A, C> lookup, BlockPos pos, Supplier<World> world, Supplier<C> ctxSupplier)
    {
        return new LazyBlockApiCache<>(world, lookup, pos, ctxSupplier);
    }

    public static <A> LazyBlockApiCache<A, Direction> of(BlockApiLookup<A, Direction> lookup, Supplier<World> world, Argument argument)
    {
        return new LazyBlockApiCache<>(world, lookup, argument.pos(), argument::face);
    }

//    public static <A, C> LazyBlockApiCache<MutateInPlace<A>, C> mutate(BlockApiLookup<MutateInPlace<A>, C> lookup, Argument argument, Supplier<ServerWorld> world)
//    {
//        return new LazyBlockApiCache<>(world, lookup, argument.pos(), () -> null);
//    }

    public static LazyBlockApiCache<Storage<ItemVariant>, Direction> itemSided(Argument argument, Supplier<World> world)
    {
        return new LazyBlockApiCache<>(world, ItemStorage.SIDED, argument.pos(), argument::face);
    }

    public static LazyBlockApiCache<Storage<FluidVariant>, Direction> fluidSided(Argument argument, Supplier<World> world)
    {
        return new LazyBlockApiCache<>(world, FluidStorage.SIDED, argument.pos(), argument::face);
    }

    public BlockPos pos()
    {
        return pos;
    }

    public C ctx()
    {
        return ctxSupplier.get();
    }
}
