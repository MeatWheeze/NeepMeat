package com.neep.neepmeat.item;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.entity.scutter.FarmingScutter;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FarmingScutterItem extends ScutterItem<FarmingScutter>
{
    public FarmingScutterItem(String registryName, Supplier<EntityType<FarmingScutter>> entityType, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, entityType, tooltipSupplier, settings);
    }

    @Override
    protected @Nullable Vec3d canPlaceAt(World world, BlockPos pos, Vec3d hit, Direction side)
    {
        Vec3d epos = super.canPlaceAt(world, pos, hit, side);
        if (epos != null && world.getBlockState(BlockPos.ofFloored(epos).down()).isOf(Blocks.FARMLAND))
            return epos;

        return null;
    }

    @Override
    protected void processEntity(FarmingScutter entity, ItemUsageContext context)
    {

        if (ItemStorage.SIDED.find(context.getWorld(), context.getBlockPos(), context.getSide()) != null)
        {
            entity.setStoragePos(context.getBlockPos());
            entity.setHomePos(BlockPos.ofFloored(entity.getPos()));
        }
    }
}
