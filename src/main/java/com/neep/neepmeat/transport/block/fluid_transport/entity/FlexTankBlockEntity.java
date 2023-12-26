package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.google.common.collect.Sets;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
