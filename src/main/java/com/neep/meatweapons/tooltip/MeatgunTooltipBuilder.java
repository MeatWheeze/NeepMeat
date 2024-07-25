package com.neep.meatweapons.tooltip;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.tooltip.TooltipBuilder;
import com.neep.meatweapons.item.meatgun.MeatgunModuleItem;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.module.AttributeContainer;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class MeatgunTooltipBuilder extends TooltipBuilder
{
    @Nullable private AmmunitionType ammoType;
    private boolean desc;

    public MeatgunTooltipBuilder()
    {

    }

    public MeatgunTooltipBuilder ammoType(AmmunitionType type)
    {
        this.ammoType = type;
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
            else if (item instanceof MeatgunModuleItem mmi)
            {
                MeatgunModule.Type<?> type = mmi.type;
                AttributeContainer attributes = type.attributes();
                tooltip.add(Text.translatable( "tooltip.meatweapons.meatgun_module_1").formatted(Formatting.GRAY));

                if (attributes.map.containsKey(MeatgunModule.Attribute.COMPLEXITY))
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.meatgun_module.complexity", attributes.get(MeatgunModule.Attribute.COMPLEXITY)).formatted(Formatting.GRAY));
                }

                if (attributes.map.containsKey(MeatgunModule.Attribute.ATTACK_DAMAGE))
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.damage", attributes.get(MeatgunModule.Attribute.ATTACK_DAMAGE)).formatted(Formatting.GRAY));
                }

                if (ammoType != null)
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.ammunition_type", ammoType.name().toUpperCase()).formatted(Formatting.GRAY));
                }

                if (attributes.map.containsKey(MeatgunModule.Attribute.AMOUNT_PER_USE))
                {
                    tooltip.add(Text.translatable("tooltip.meatweapons.per_use", attributes.get(MeatgunModule.Attribute.AMOUNT_PER_USE)).formatted(Formatting.GRAY));
                }

                if (attributes.map.containsKey(MeatgunModule.Attribute.COOLDOWN))
                {
                    TooltipSupplier.wrapLines(tooltip, Text.translatable("tooltip.meatweapons.cooldown", attributes.get(MeatgunModule.Attribute.COOLDOWN)).formatted(Formatting.GRAY));
                }

                if (desc)
                {
                    tooltip.add(Text.translatable(item.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
                }
            }
        };
    }
}
