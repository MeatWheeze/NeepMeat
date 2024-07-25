package com.neep.meatweapons.tooltip;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.tooltip.TooltipBuilder;
import com.neep.meatweapons.meatgun.AmmunitionType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class MeatgunTooltipBuilder extends TooltipBuilder
{
    private int cooldown = -1;
    private int perUse = -1;
    @Nullable private AmmunitionType ammoType;
    private boolean desc;
    private float damage = -1;

    public MeatgunTooltipBuilder()
    {

    }

    public MeatgunTooltipBuilder cooldown(int cooldown)
    {
        this.cooldown = cooldown;
        return this;
    }

    public MeatgunTooltipBuilder ammoType(AmmunitionType type)
    {
        this.ammoType = type;
        return this;
    }
    public MeatgunTooltipBuilder perUse(int per)
    {
        this.perUse = per;
        return this;
    }

    public MeatgunTooltipBuilder damage(float damage)
    {
        this.damage = damage;
        return this;
    }

    public MeatgunTooltipBuilder desc()
    {
        this.desc = true;
        return this;
    }

    @Override
    public TooltipSupplier build()
    {
        return (item, tooltip) ->
        {
            if (!Screen.hasShiftDown())
            {
                TooltipSupplier.shiftForInfo(tooltip);
            }
            else
            {
                tooltip.add(Text.translatable( "tooltip.meatweapons.meatgun_module_1").formatted(Formatting.GRAY));

                if (damage != -1)
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.damage", damage).formatted(Formatting.GRAY));
                }

                if (ammoType != null)
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.ammunition_type", ammoType.name().toUpperCase()).formatted(Formatting.GRAY));
                }

                if (perUse != -1)
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.per_use", perUse).formatted(Formatting.GRAY));
                }

                if (cooldown != -1)
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.cooldown", cooldown).formatted(Formatting.GRAY));
                }

                if (desc)
                {
                    tooltip.add(Text.translatable(item.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
                }
            }
        };
    }
}
