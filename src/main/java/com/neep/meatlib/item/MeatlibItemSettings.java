package com.neep.meatlib.item;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.Rarity;

// HAHAHHAHAHAHA I'M BRINGING BACK Settings::group! YOU CAN'T STOP ME!
public class MeatlibItemSettings extends FabricItemSettings
{
    public MeatlibItemSettings equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
        FabricItemInternals.computeExtraData(this).equipmentSlot(equipmentSlotProvider);
        return this;
    }

    public MeatlibItemSettings customDamage(CustomDamageHandler handler) {
        FabricItemInternals.computeExtraData(this).customDamage(handler);
        return this;
    }

    // Overrides of vanilla methods

    @Override
    public MeatlibItemSettings food(FoodComponent foodComponent) {
        super.food(foodComponent);
        return this;
    }

    @Override
    public MeatlibItemSettings maxCount(int maxCount) {
        super.maxCount(maxCount);
        return this;
    }

    @Override
    public MeatlibItemSettings maxDamageIfAbsent(int maxDamage) {
        super.maxDamageIfAbsent(maxDamage);
        return this;
    }

    @Override
    public MeatlibItemSettings maxDamage(int maxDamage) {
        super.maxDamage(maxDamage);
        return this;
    }

    @Override
    public MeatlibItemSettings recipeRemainder(Item recipeRemainder) {
        super.recipeRemainder(recipeRemainder);
        return this;
    }

    @Override
    public MeatlibItemSettings rarity(Rarity rarity) {
        super.rarity(rarity);
        return this;
    }

    @Override
    public MeatlibItemSettings fireproof() {
        super.fireproof();
        return this;
    }

    @Override
    public MeatlibItemSettings requires(FeatureFlag... features) {
        super.requires(features);
        return this;
    }

    public MeatlibItemSettings group(ItemGroup group)
    {
        return this;
    }
}
