package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SmallCompressorMinecart extends AbstractMinecartEntity
{
//    private final ImplementedInventory inventory = ImplementedInventory.ofSize(1);

    public SmallCompressorMinecart(EntityType<?> entityType, World world)
    {
        super(entityType, world);
    }

    public SmallCompressorMinecart(World world, double x, double y, double z)
    {
        super(NMEntities.SMALL_COMPRESSOR_MINECART, world, x, y, z);
    }

    @Override
    public BlockState getDefaultContainedBlock()
    {
        return NMBlocks.SMALL_COMPRESSOR.getDefaultState();
    }

    @Override
    protected Item getItem()
    {
        return NMItems.SMALL_COMPRESSOR_MINECART;
    }

    @Override
    public Type getMinecartType()
    {
        return null;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        return super.interact(player, hand);
    }

    @Override
    public ItemStack getPickBlockStack()
    {
        return NMItems.SMALL_COMPRESSOR_MINECART.getDefaultStack();
    }
}
