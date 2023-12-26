package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class FlexTankBlockEntity extends SyncableBlockEntity
{
    private Set<BlockPos> children = Sets.newHashSet();
    @Nullable private BlockPos root;

    private final WritableSingleFluidStorage storage = new WritableSingleFluidStorage(0, this::markDirty)
    {
        @Override
        protected long getCapacity(FluidVariant variant)
        {
            return children.size() * 8 * FluidConstants.BUCKET;
        }
    };

    public FlexTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public static void updateConnections(World world, FlexTankBlockEntity origin)
    {
        var found = findThings(world, origin.getPos());

        if (!found.isEmpty())
        {
            FlexTankBlockEntity root = null;
            int lowest = Integer.MAX_VALUE;
            for (var be : found)
            {
                if (root == null)
                    root = be;

                BlockPos bePos = be.getPos();
                if (bePos.getY() < lowest)
                {
                    lowest = bePos.getY();
                    root = be;
                }
            }

            root.markRoot(found.stream().map(BlockEntity::getPos).collect(Collectors.toSet()));

            for (var be : found)
            {
                be.setRoot(root);
            }
        }
    }

    protected static Set<FlexTankBlockEntity> findThings(World world, BlockPos start)
    {
        Set<FlexTankBlockEntity> found = Sets.newHashSet();
        Queue<BlockPos> queue = Queues.newArrayDeque();
        Set<BlockPos> visited = Sets.newHashSet();

        queue.add(start);
        visited.add(start);

        if (world.getBlockEntity(start) instanceof FlexTankBlockEntity be)
        {
            found.add(be);
        }

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (visited.contains(mutable))
                    continue;

                visited.add(mutable.toImmutable());

                if (world.getBlockEntity(mutable) instanceof FlexTankBlockEntity be)
                {
                    found.add(be);
                    queue.add(mutable.toImmutable());
                }
            }
        }

        return found;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        if (root != null)
        {
            nbt.put("root", NbtHelper.fromBlockPos(root));
        }

        if (children != null)
        {
            NbtList list = new NbtList();
            for (var child : children)
            {
                NbtCompound c = NbtHelper.fromBlockPos(child);
                list.add(c);
            }

            nbt.put("children", list);
        }

        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.contains("root"))
        {
            this.root = NbtHelper.toBlockPos(nbt.getCompound("root"));
        }

        if (nbt.contains("children"))
        {
            NbtList list = nbt.getList("children", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); ++i)
            {
                children.add(NbtHelper.toBlockPos(list.getCompound(i)));
            }
        }

        storage.readNbt(nbt);
    }

    public Storage<FluidVariant> getStorage(Direction direction)
    {
        FlexTankBlockEntity root = getRoot();
        if (isRoot())
        {
            return storage;
        }
        else if (root != null)
        {
            return getRoot().getStorage(direction);
        }
        return Storage.empty();
    }

    public void markRoot(Set<BlockPos> children)
    {
        this.children = children;
    }

    public boolean isRoot()
    {
        return this == getRoot();
    }

    public void setRoot(FlexTankBlockEntity root)
    {
        if (root != this)
        {
            this.children.clear();

            if (storage.getAmount() != 0)
            {
                // If this used to be the root, move the fluid to the new root.
                try (Transaction transaction = Transaction.openOuter())
                {
                    // Excess fluid should be deleted if capacity is lower
                    root.storage.insert(storage.variant, storage.amount, transaction);

                    storage.amount = 0;
                    storage.variant = FluidVariant.blank();
                    transaction.commit();
                }
            }
        }

        this.root = root.getPos();
    }

    @Nullable
    public FlexTankBlockEntity getRoot()
    {
        if (root != null && world.getBlockEntity(root) instanceof FlexTankBlockEntity be)
        {
            return be;
        }
        return null;
    }

    public int getSize()
    {
        if (!isRoot())
        {
            return getRoot().getSize();
        }
        else
        {
            return children.size();
        }
    }
}
