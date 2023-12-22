package com.neep.neepmeat.item;

import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class GogglesItem extends ArmorItem implements MeatlibItem, IAnimatable
{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    protected final String registryName;

    public GogglesItem(String name , ArmorMaterial material, EquipmentSlot slot, Settings settings)
    {
        super(material, slot, settings);
        this.registryName = name;
        ItemRegistry.queueItem(this);
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
//        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.goggles.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
