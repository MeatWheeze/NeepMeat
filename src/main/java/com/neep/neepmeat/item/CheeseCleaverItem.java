package com.neep.neepmeat.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CheeseCleaverItem extends AnimatedSword implements ISyncable, IAnimatable
{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static String CONTROLLER_NAME = "controller";

    public CheeseCleaverItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.DIAMOND, 4, -3f, settings);
        GeckoLibNetwork.registerSyncable(this);
    }

    public static void writeCharged(ItemStack stack, boolean charged)
    {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("charged", charged);
        stack.writeNbt(nbt);
    }

    @Override
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(new AnimationController<>(this, CONTROLLER_NAME, 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        super.postHit(stack, target, attacker);

        if (stack.getOrCreateNbt().getBoolean("charged"))
        {
            target.addVelocity(0, 0.4, 0);
            writeCharged(stack, false);
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        user.setCurrentHand(hand);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);

        controller.transitionLengthTicks = 1;
        switch (state)
        {
            case ANIM_SWING -> {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.cheese_cleaver.swing"));
            }
            case ANIM_STAB -> {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.cheese_cleaver.stab"));
            }
            case ANIM_CHOP -> {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.cheese_cleaver.chop"));

            }
        }
    }

    @Override
    public boolean onSwing(ItemStack stack, PlayerEntity player)
    {
//        onAnimationSync(GeckoLibUtil.getIDFromStack(stack), AnimatedSword.ANIM_SWING);
//        return false;
        return true;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        if (stack.getOrCreateNbt().getBoolean("charged"))
        {
            return UseAction.NONE;
        }
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
       writeCharged(stack, true);
       return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 20;
    }
}
