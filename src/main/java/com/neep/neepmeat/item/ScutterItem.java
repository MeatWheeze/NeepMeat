package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.entity.scutter.FarmingScutter;
import com.neep.neepmeat.entity.scutter.ScutterEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class ScutterItem<T extends ScutterEntity> extends BaseItem
{
    protected final Supplier<EntityType<T>> entityType;

    public ScutterItem(String registryName, Supplier<EntityType<T>> entityType, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
        this.entityType = entityType;
    }

    @Nullable
    protected Vec3d canPlaceAt(World world, BlockPos pos, Vec3d hit, Direction side)
    {
        BlockState state = world.getBlockState(pos);
        BlockPos offset = pos;
        if (state.blocksMovement())
        {
            offset = pos.offset(side);
        }
        Vec3d entityPos = Vec3d.ofCenter(offset);

        Box box = entityType.get().getDimensions().getBoxAt(entityPos);
        if (world.isSpaceEmpty(box))
        {
            return entityPos;
        }

        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Vec3d valid = canPlaceAt(world, pos, context.getHitPos(), context.getSide());

        if (valid != null)
        {
            if (world.isClient())
            {
                return ActionResult.SUCCESS;
            }

            T scutter = entityType.get().create(world);
            if (scutter != null)
            {
                world.spawnEntity(scutter);
                scutter.setPos(valid.x, valid.y, valid.z);
                processEntity(scutter, context);
            }

            return ActionResult.SUCCESS;

        }
        return super.useOnBlock(context);
    }

    protected void processEntity(T entity, ItemUsageContext context)
    {

    }
}
