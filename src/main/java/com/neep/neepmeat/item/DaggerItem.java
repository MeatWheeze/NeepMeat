package com.neep.neepmeat.item;

import com.neep.meatweapons.Util;
import com.neep.neepmeat.fluid.RealisticFluid;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.meatlib.item.BaseSwordItem;
import com.neep.neepmeat.init.NMItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class DaggerItem extends BaseSwordItem
{
    public static final float MAX_HEALTH = 3;

    public DaggerItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.GOLD, 4, 1f, settings.maxDamage(128));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        super.postHit(stack, target, attacker);

        World world = target.getEntityWorld();
        if (target.isDead() && !world.isClient)
        {
            BlockPos pos = target.getBlockPos();
            if (world.getBlockState(pos.offset(Direction.DOWN)).isOf(NMBlocks.FLUID_DRAIN))
            {
                RealisticFluid.incrementLevel(world, pos, world.getBlockState(pos), NMFluids.FLOWING_BLOOD);
            }
        }
        return true;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        if (!world.isClient)
        {
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 100000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        if (user instanceof PlayerEntity player && !world.isClient() && this.getMaxUseTime(stack) - remainingUseTicks > 10)
        {
            Vec3d pos = user.getEyePos();
            Vec3d end = pos.add(user.getRotationVec(0.5f).multiply(20));
            Optional<LivingEntity> optional = getTarget(player, user.getEyePos(), end, 2);
            if (optional.isPresent())
            {
                LivingEntity entity = optional.get();
                entity.damage(DamageSource.player(player), 3);
                if (entity.getHealth() <= MAX_HEALTH)
                {
                    spawnSpecialDrop(world, entity.getPos(), entity);
                    entity.kill();
                }
            }
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity) user;
    }

    public static void spawnSpecialDrop(World world, Vec3d pos, LivingEntity entity)
    {
        if (entity instanceof ZombieEntity)
        {
            ItemEntity item = new ItemEntity(world, pos.x, pos.y, pos.z, NMItems.REANIMATED_HEART.getDefaultStack());
            world.spawnEntity(item);
        }
    }

    public Optional<LivingEntity> getTarget(PlayerEntity caster, Vec3d start, Vec3d end, double distance)
    {
        World world = caster.world;
        if (!world.isClient)
        {
            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.collides() && entity instanceof LivingEntity;

            double minDistance = distance;
            Entity entity = null;
            for (EntityHitResult result : Util.getRayTargets(caster, start, blockResult.getPos(), entityFilter, 0.1))
            {
                if (result.getPos().distanceTo(start) < minDistance)
                {
                    minDistance = result.getPos().distanceTo(start);
                    entity = result.getEntity();
                }
            }

            return Optional.ofNullable((LivingEntity) entity);
        }
        return Optional.empty();
    }

//    @Override
//    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
//    {
//        if (entity instanceof ZombieEntity)
//        {
//            System.out.println("ooooooo");
//            return ActionResult.SUCCESS;
//        }
//        return ActionResult.FAIL;
//    }
}
