package com.neep.neepmeat.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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

public class SlasherItem extends AnimatedSword implements IAnimatable, ISyncable
{
    public AnimationFactory factory = new AnimationFactory(this);
    public static String CONTROLLER_NAME = "controller";

    public SlasherItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.DIAMOND, 0, -1.2f, settings);
        GeckoLibNetwork.registerSyncable(this);
//        ItemInit.putItem(registryName, this);
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
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (!world.isClient)
        {
            final int id = GeckoLibUtil.guaranteeIDForStack(user.getStackInHand(hand), (ServerWorld) world);

            if (user.isSprinting())
//            if (false)
            {
                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_STAB);
            }
            else
            {
                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_SWING);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);

        final PlayerEntity player = MinecraftClient.getInstance().player;
//        if (player != null)
//        {
//            player.sendMessage(Text.of("Slashing!"), true);
//        }

        controller.transitionLengthTicks = 1;
        switch (state)
        {
            case ANIM_SWING:
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.swing", false));
                break;
            case ANIM_STAB:
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.stab", false));
                break;
            default:


        }
    }
}
