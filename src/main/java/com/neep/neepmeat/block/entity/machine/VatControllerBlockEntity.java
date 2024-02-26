package com.neep.neepmeat.block.entity.machine;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.transfer.MultiFluidBuffer;
import com.neep.neepmeat.api.multiblock.ControllerBlockEntity;
import com.neep.neepmeat.api.multiblock.MultiBlock;
import com.neep.neepmeat.api.storage.MultiItemBuffer;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.block.vat.VatComponent;
import com.neep.neepmeat.block.vat.VatControllerBlock;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class VatControllerBlockEntity extends SyncableBlockEntity implements ControllerBlockEntity
{
    protected boolean assembled;
    public List<BlockPos> blocks;
    public List<BlockPos> ports;
    protected Storages storages;

    public VatControllerBlockEntity(BlockPos pos, BlockState state)
    {
        this(null, pos, state);
    }

    public VatControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storages = new Storages(this);
        blocks = new ArrayList<>();
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.assembled = nbt.getBoolean("assembled");
        NbtList list = (NbtList) nbt.get("blocks");
        if (list != null)
            blocks.addAll(list.stream().map(element -> NbtHelper.toBlockPos((NbtCompound) element)).collect(Collectors.toList()));

        storages.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("assembled", isAssembled());
        NbtList list = new NbtList();
        if (blocks != null)
            list.addAll(blocks.stream().map(NbtHelper::fromBlockPos).collect(Collectors.toList()));
        nbt.put("blocks", list);

        storages.writeNbt(nbt);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
    }

    public boolean tryAssemble(ServerWorld world)
    {
        BlockPos origin = getPos();
        Direction facing = getCachedState().get(BaseHorFacingBlock.FACING);

        BlockPos centre = origin.offset(facing.getOpposite());

        this.blocks = checkValid(world, centre);

        if (blocks == null)
            return false;

        blocks.remove(this.getPos());

        blocks.stream()
                .map(pos1 -> world.getBlockEntity(pos1) instanceof MultiBlock.Entity entity ? entity : null)
                .filter(Objects::nonNull)
                .forEach(be -> {be.setController(getPos());});

        world.setBlockState(getPos(), getCachedState().with(VatControllerBlock.ASSEMBLED, true), Block.NOTIFY_LISTENERS);
        this.assembled = true;
        markDirty();
        return true;
    }

    public boolean disassemble(ServerWorld world, boolean replaced)
    {
        blocks.stream().map(world::getBlockEntity).filter(Objects::nonNull).forEach(be -> {if (be instanceof MultiBlock.Entity entity) entity.setController(null);});
        if (!replaced)
            world.setBlockState(getPos(), getCachedState().with(VatControllerBlock.ASSEMBLED, false), Block.NOTIFY_LISTENERS);
        blocks.clear();
        this.assembled = false;
        dropItems(world);
        markDirty();
        System.out.println("Disassemble");
        return true;
    }

    protected void dropItems(World world)
    {
        Transaction transaction = Transaction.openOuter();
        for (StorageView<ItemVariant> view : storages.items)
        {
            if (view.isResourceBlank())
                continue;

            ItemScatterer.spawn(world, getPos().getX(), getPos().getY(), getPos().getZ(), view.getResource().toStack((int) view.getAmount()));
            view.extract(view.getResource(), Long.MAX_VALUE, transaction);
        }
        transaction.commit();
    }

    public static List<BlockPos> checkValid(ServerWorld world, BlockPos centre)
    {
        boolean valid = true;
        List<BlockPos> blocks = new ArrayList<>();
        for (int i = 0; i < 3; ++i)
        {
            List<BlockPos> list = checkOddSquare(world, centre.add(0, i, 0), 1, state -> state.getBlock() instanceof VatComponent);
            if (list != null)
            {
                blocks.addAll(list);
            }
            else
            {
                valid = false;
            }
        }

        List<BlockPos> list = checkOddRing(world, centre.add(0, 3, 0), 1, state -> state.getBlock() instanceof VatComponent);
        if (list != null)
        {
            blocks.addAll(list);
        }
        else
        {
            valid = false;
        }

//        valid = valid && world.getBlockState(centre.add(0, 3, 0)).isOf(NMBlocks.AGITATOR);

        valid = valid && blocks.stream().filter(pos1 -> world.getBlockState(pos1).getBlock() instanceof VatControllerBlock).count() == 1;

        if (!valid)
            return null;

        return blocks;
    }

    public static List<BlockPos> checkOddSquare(World world, BlockPos centre, int radius, Predicate<BlockState> predicate)
    {
        BlockPos.Mutable mutable = centre.mutableCopy();
        List<BlockPos> list = new ArrayList<>();
        int y = centre.getY();
        for (int i = - radius; i < radius + 1; ++i)
        {
            for (int j = - radius; j < radius + 1; ++j)
            {
                mutable.set(centre.getX() + i, y, centre.getZ() + j);
                BlockState state = world.getBlockState(mutable);
                if (!predicate.test(state))
                {
                    return null;
                }
                list.add(mutable.toImmutable());
            }
        }
        return list;
    }

    public static List<BlockPos> checkOddRing(World world, BlockPos centre, int radius, Predicate<BlockState> predicate)
    {
        BlockPos.Mutable mutable = centre.mutableCopy();
        List<BlockPos> list = new ArrayList<>();
        int y = centre.getY();
        for (int i = - radius; i < radius + 1; i += (radius * 2))
        {
            for (int j = - radius; j < radius + 1; ++j)
            {
                mutable.set(centre.getX() + i, y, centre.getZ() + j);
                BlockState state = world.getBlockState(mutable);
                if (!predicate.test(state))
                {
                    return null;
                }
                list.add(mutable.toImmutable());
            }
            for (int j = - radius + 1; j < radius; ++j)
            {
                mutable.set(centre.getX() + j, y, centre.getZ() + i);
                BlockState state = world.getBlockState(mutable);
                if (!predicate.test(state))
                {
                    return null;
                }
                list.add(mutable.toImmutable());
            }
        }
        return list;
    }

    public Storage<FluidVariant> getFluidStorage()
    {
        return storages.fluids;
    }

    public Storage<ItemVariant> getItemStorage()
    {
        return storages.items;
    }

    public boolean isAssembled()
    {
        return assembled;
    }

    @Override
    public <V extends TransferVariant<?>> Storage<V> getStorage(Class<V> variant)
    {
//        System.out.println(variant);
//        System.out.println("item: " + variant.equals(ItemVariant.class));
//        System.out.println("fluid: " + variant.equals(FluidVariant.class));
        if (variant.equals(ItemVariant.class))
        {
            return (Storage<V>) storages.items;
        }

        if (variant.equals(FluidVariant.class))
        {
            return (Storage<V>) storages.fluids;
        }

        return Storage.empty();
    }

    @Override
    public void componentBroken(ServerWorld world)
    {
        disassemble(world, false);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static class Storages
    {
        protected WritableStackStorage itemInput;
        protected WritableStackStorage itemOutput;
        protected MultiItemBuffer items;

        protected MultiFluidBuffer fluids;

        protected Storages(SyncableBlockEntity parent)
        {
            itemInput = new WritableStackStorage(parent::sync);
            itemOutput = new WritableStackStorage(parent::sync);
            items = new MultiItemBuffer(List.of(itemInput, itemOutput));

//            fluidInput = new WritableFluidBuffer(parent, 2 * FluidConstants.BUCKET);
//            fluidOutput = new WritableFluidBuffer(parent, 2 * FluidConstants.BUCKET);
//            fluids = new CombinedFluidStorage(List.of(fluidInput, fluidOutput));
            fluids = new MultiFluidBuffer(16 * FluidConstants.BUCKET, type -> true, parent::sync);
        }

        public void readNbt(NbtCompound nbt)
        {
            items.readNbt(nbt);
            fluids.readNbt(nbt);
        }

        public NbtCompound writeNbt(NbtCompound nbt)
        {
            items.writeNbt(nbt);
            fluids.writeNbt(nbt);
            return nbt;
        }
    }
}
