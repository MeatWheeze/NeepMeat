package com.neep.neepmeat.item;

import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class GogglesItem extends ArmorItem implements MeatlibItem, IAnimatable
{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private final String registryName;
    private final TooltipSupplier tooltip = TooltipSupplier.simple(1);

    public GogglesItem(String name , ArmorMaterial material, EquipmentSlot slot, Settings settings)
    {
        super(material, slot, settings);
        this.registryName = name;
        ItemRegistry.queue(this);
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        this.tooltip.apply(this, tooltip);
        super.appendTooltip(stack, world, tooltip, context);
    }
}
