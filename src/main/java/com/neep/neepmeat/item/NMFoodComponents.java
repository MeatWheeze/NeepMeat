package com.neep.neepmeat.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class NMFoodComponents
{
    public static final FoodComponent ENLIGHTENED_BRAIN = new FoodComponent.Builder().hunger(6).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 1), 1).meat().build();
    public static final FoodComponent MEAT_SCRAP = new FoodComponent.Builder().hunger(1).saturationModifier(0.3f).meat().build();
    public static final FoodComponent MEAT_BRICK = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.7f).meat().build();
    public static final FoodComponent COOKED_MEAT_BRICK = new FoodComponent.Builder().hunger(10).saturationModifier(0.8f).meat().build();
}
