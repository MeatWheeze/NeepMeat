package com.neep.neepmeat.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class NMFoodComponents
{
    public static final FoodComponent ENLIGHTENED_BRAIN = new FoodComponent.Builder().hunger(6).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 1), 1).meat().build();
    public static final FoodComponent MEAT_SCRAP = new FoodComponent.Builder().hunger(1).saturationModifier(0.3f).meat().build();
    public static final FoodComponent MEAT_BRICK = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.7f).meat().build();
    public static final FoodComponent COOKED_MEAT_BRICK = new FoodComponent.Builder().hunger(8).saturationModifier(0.8f).meat().build();
    public static final FoodComponent WHISPER_BREAD = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 1), 0.3f).build();
    public static final FoodComponent PINKDRINK = new FoodComponent.Builder().hunger(3).saturationModifier(0.2f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30, 1), 1f).build();
    public static final FoodComponent BLOOD_BUBBLE = new FoodComponent.Builder().hunger(3).saturationModifier(0.2f).statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, 1), 1f).build();
    public static final FoodComponent MILK_BOTTLE = new FoodComponent.Builder().hunger(5).saturationModifier(0.2f).build();
    public static final FoodComponent MEAT_CARTON = new FoodComponent.Builder().hunger(0).saturationModifier(0.0f).build();
}
