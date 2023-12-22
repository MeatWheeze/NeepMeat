package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.block.multiblock.IControllerBlockEntity;
import com.neep.neepmeat.block.multiblock.IMultiBlock;
import com.neep.neepmeat.block.multiblock.IPortBlock;
import com.neep.neepmeat.block.vat.IVatComponent;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.block.vat.VatControllerBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VatControllerBlockEntity extends BlockEntity implements IControllerBlockEntity
{
    protected boolean assembled;
    public List<BlockPos> blocks;
    public List<BlockPos> ports;

    public VatControllerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.VAT_CONTROLLER, pos, state);
    }

    public VatControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        blocks = new ArrayList<>();
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        NbtList list = (NbtList) nbt.get("blocks");
        if (list != null)
            blocks.addAll(list.stream().map(element -> NbtHelper.toBlockPos((NbtCompound) element)).collect(Collectors.toList()));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        NbtList list = new NbtList();
        if (blocks != null)
            list.addAll(blocks.stream().map(NbtHelper::fromBlockPos).collect(Collectors.toList()));
        nbt.put("blocks", list);
        return nbt;
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
//        if (world instanceof ServerWorld serverWorld && world.getServer().isOnThread())
//        {
//            tryAssemble(serverWorld);
//        }
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
                .map(pos1 -> world.getBlockEntity(pos1) instanceof IMultiBlock.Entity entity ? entity : null)
                .filter(Objects::nonNull)
                .forEach(be -> {be.setController(getPos());});

        world.setBlockState(getPos(), getCachedState().with(VatControllerBlock.ASSEMBLED, true), Block.NOTIFY_LISTENERS);
        markDirty();
        return true;
    }

    public boolean disassemble(ServerWorld world)
    {
        blocks.stream().map(world::getBlockEntity).filter(Objects::nonNull).forEach(be -> ((IMultiBlock.Entity) be).setController(null));
        world.setBlockState(getPos(), getCachedState().with(VatControllerBlock.ASSEMBLED, false), Block.NOTIFY_LISTENERS);
        blocks.clear();
        markDirty();
        System.out.println("Disassemble");
        return true;
    }

    public static List<BlockPos> checkValid(ServerWorld world, BlockPos centre)
    {
        boolean valid = true;
        List<BlockPos> blocks = new ArrayList<>();
        for (int i = 0; i < 3; ++i)
        {
            List<BlockPos> list = checkOddSquare(world, centre.add(0, i, 0), 1, state -> state.getBlock() instanceof IVatComponent);
            if (list != null)
            {
                blocks.addAll(list);
            }
            else
            {
                valid = false;
            }
        }

        List<BlockPos> list = checkOddRing(world, centre.add(0, 3, 0), 1, state -> state.getBlock() instanceof IVatComponent);
        if (list != null)
        {
            blocks.addAll(list);
        }
        else
        {
            valid = false;
        }

        valid = valid && world.getBlockState(centre.add(0, 3, 0)).isOf(NMBlocks.AGITATOR);

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
        return null;
    }
    public Storage<ItemVariant> getItemStorage()
    {
        return null;
    }

    public boolean isAssembled()
    {
        return assembled;
    }

    @Override
    public <V extends TransferVariant<?>> Storage<V> getStorage(Class<V> variant)
    {
        return null;
    }

    @Override
    public void componentBroken(ServerWorld world)
    {
        disassemble(world);
    }
}
