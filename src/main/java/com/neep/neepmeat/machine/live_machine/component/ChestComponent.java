package com.neep.neepmeat.machine.live_machine.component;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

public class ChestComponent implements LivingMachineComponent
{
    private final ChestBlockEntity be;
    private final InventoryStorage inventoryStorage;

    public ChestComponent(ChestBlockEntity be)
    {
        this.be = be;
        this.inventoryStorage = InventoryStorage.of(be, null);
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return be.isRemoved();
    }

    @Override
    public ComponentType<?> getComponentType()
    {
        return LivingMachineComponents.ITEM_OUTPUT;
    }

    public InventoryStorage getStorage()
    {
        return inventoryStorage;
    }

    public ChestBlockEntity get()
    {
        return be;
    }
}
