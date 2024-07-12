package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.screen_handler.AdvancedRouterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AdvancedRouterBlockEntity extends RouterBlockEntity
{
    public static final int MAX_FILTERS = 2;

    private final List<FilterList> filters = Util.make(() ->
    {
        List<FilterList> filters = Arrays.asList(new FilterList[6]);
        filters.replaceAll(ignored -> new FilterList(MAX_FILTERS));
        return filters;
    });

    public AdvancedRouterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        NbtList list = new NbtList();

        for (var filter : filters)
        {
            list.add(filter.writeNbt(new NbtCompound()));
        }

        nbt.put("filters", list);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        NbtList list = nbt.getList("filters", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            FilterList filterList = new FilterList(MAX_FILTERS);
            filterList.readNbt(list.getCompound(i));
            filters.set(i, filterList);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new AdvancedRouterScreenHandler(syncId, inv);
    }
}
