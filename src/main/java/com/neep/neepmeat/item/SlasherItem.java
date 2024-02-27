package com.neep.neepmeat.item;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.client.renderer.SwordRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SlasherItem extends AnimatedSword implements GeoItem
{
    public static String CONTROLLER_NAME = "controller";
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> rendererProvider = GeoItem.makeRenderer(this);

    public SlasherItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.DIAMOND, 0, -1.2f, settings);
    }

    private PlayState predicate(AnimationState<SlasherItem> event)
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
        if (world instanceof ServerWorld serverWorld)
        {
            long id = GeoItem.getOrAssignId(user.getStackInHand(hand), serverWorld);
//            triggerAnim(user, id, );

//            if (user.isSprinting())
//            {
//                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_STAB);
//            }
//            else
//            {
//                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_SWING);
//            }
        }
        return super.use(world, user, hand);
    }

//    @Override
//    public void onAnimationSync(int id, int state)
//    {
//        final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);
//
//        controller.transitionLengthTicks = 1;
//        switch (state)
//        {
//            case ANIM_SWING:
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.swing"));
//                break;
//            case ANIM_STAB:
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.stab"));
//                break;
//            default:
//
//
//        }
//    }

    @Override
    public AnimationQueue getQueue()
    {
        return null;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private SwordRenderer<SlasherItem> renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer()
            {
                if (this.renderer == null)
                    this.renderer = new SwordRenderer<>(
                            new GenericModel<>(
                                    NeepMeat.NAMESPACE,
                                    "geo/slasher.geo.json",
                                    "textures/item/slasher.png",
                                    "animations/slasher.animation.json"

                            )
                    );

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return rendererProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 20, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }
}
