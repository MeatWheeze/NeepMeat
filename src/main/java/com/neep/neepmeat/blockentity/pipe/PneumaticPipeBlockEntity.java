package com.neep.neepmeat.blockentity.pipe;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.util.PipeOffset;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PneumaticPipeBlockEntity extends BlockEntity implements Storage<ItemVariant>
{
    protected List<Pair<PipeOffset, ItemStack>> items = new ArrayList<>();

    public PneumaticPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PNEUMATIC_PIPE, pos, state);
        items.add(new Pair<>(new PipeOffset(Direction.NORTH, Direction.UP), new ItemStack(Items.COBBLED_DEEPSLATE)));
    }

    public PneumaticPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        return null;
    }

    public List<Pair<PipeOffset, ItemStack>> getItems()
    {
        return items;
    }
}
