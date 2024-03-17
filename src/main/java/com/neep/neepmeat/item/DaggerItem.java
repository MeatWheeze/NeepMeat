package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseSwordItem;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatweapons.Util;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.recipe.VivisectionRecipe;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidDrainBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
            if (world.getBlockEntity(pos.down()) instanceof FluidDrainBlockEntity be)
            {
//                RealisticFluid.incrementLevel(world, pos, world.getBlockState(pos), NMFluids.FLOWING_BLOOD);
                try (Transaction transaction = Transaction.openOuter())
                {
                    be.getBuffer(Direction.UP).insert(FluidVariant.of(NMFluids.STILL_BLOOD), 20250, transaction);
                    transaction.commit();
                    spawnBloodParticles(target.getPos(), (ServerWorld) world);
                }
            }
        }
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)
    {
        if (state.isOf(NMBlocks.INTEGRATOR_EGG))
        {

        }
        return super.postMine(stack, world, state, pos, miner);
    }

    public static void spawnBloodParticles(Vec3d entityPos, ServerWorld world)
    {
//        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, NMFluids.BLOOD.getDefaultState()),
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.NETHER_WART_BLOCK.getDefaultState()),
                entityPos.x, entityPos.y, entityPos.z,
                30,
                0.1, 1, 0.1,
                0.1);
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        if (world.isClient())
        {
            if (remainingUseTicks % 10 == 0)
            {
                user.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1, 1);
                user.playSound(SoundEvents.ITEM_HONEYCOMB_WAX_ON, 1, 1);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 40;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (user instanceof PlayerEntity player && !world.isClient())
        {
            Optional<LivingEntity> optional = getEntity(player);
            BlockPos pos = getBlock(player);
            if (optional.isPresent())
            {
                LivingEntity entity = optional.get();
                entity.damage(DamageSource.player(player), 5);

                tryRecipe(world, entity.getPos(), entity);
//                if (entity.getHealth() <= MAX_HEALTH)
//                {
//                    entity.kill();
//                }
            }
            else if (pos != null && world.getBlockState(pos).isOf(NMBlocks.INTEGRATOR_EGG))
            {
                world.breakBlock(pos, false);
                ItemStack stack1 = NMItems.CHRYSALIS.getDefaultStack();
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack1);
                world.spawnEntity(itemEntity);
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), NMSounds.VIVISECTION_COMPLETE, SoundCategory.PLAYERS, 1, 1);
            }
            return stack;
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        if (getEntity(user).isPresent() || getBlock(user) != null)
        {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    public static void tryRecipe(World world, Vec3d pos, LivingEntity entity)
    {
        var ctx = new VivisectionRecipe.VivisectionContext(world, entity);
        VivisectionRecipe recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.VIVISECTION, ctx).orElse(null);
        if (recipe != null)
        {
            recipe.ejectOutputs(ctx, null);
            world.playSound(null, pos.x, pos.y, pos.z, NMSounds.VIVISECTION_COMPLETE, SoundCategory.PLAYERS, 1, 1);
        }
    }

    protected Optional<LivingEntity> getEntity(PlayerEntity user)
    {
        Vec3d pos = user.getEyePos();
        Vec3d end = pos.add(user.getRotationVec(0.5f).multiply(20));
        return getTarget(user, user.getEyePos(), end, 4);
    }

    public Optional<LivingEntity> getTarget(PlayerEntity caster, Vec3d start, Vec3d end, double distance)
    {
        World world = caster.world;
        if (!world.isClient)
        {
            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.canHit() && entity instanceof LivingEntity;

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

    @Nullable
    protected BlockPos getBlock(PlayerEntity user)
    {
        Vec3d pos = user.getEyePos();
        Vec3d end = pos.add(user.getRotationVec(0.5f).multiply(4));
        return getBlockTarget(pos, end, user.getWorld(), user);
    }

    @Nullable
    protected BlockPos getBlockTarget(Vec3d start, Vec3d end, World world, Entity entity)
    {
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
        var result = world.raycast(context);

        // Jank!
        if (result.getType() != HitResult.Type.MISS && world.getBlockState(result.getBlockPos()).isOf(NMBlocks.INTEGRATOR_EGG))
        {
            return result.getBlockPos();
        }
        return null;
    }
}
