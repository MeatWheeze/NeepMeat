package com.neep.neepmeat.init;

import com.google.common.base.Suppliers;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum NMToolMaterials implements ToolMaterial
{
    MEAT_STEEL(MiningLevels.DIAMOND, 1500, 7.0F, 2F, 14, () -> Ingredient.ofItems(NMItems.MEAT_STEEL)),
    EMBOSSED_MEAT_STEEL(MiningLevels.DIAMOND, 1500, 7.0F, 2F, 22, () -> Ingredient.ofItems(NMItems.MEAT_STEEL))

    ;

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;

    private final Supplier<Ingredient> repairIngredient;

    NMToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient)
    {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get); // Hmmmmmmmm
    }

    @Override
    public int getDurability()
    {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier()
    {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage()
    {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel()
    {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability()
    {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return this.repairIngredient.get();
    }
}
