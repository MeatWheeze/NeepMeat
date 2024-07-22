package com.neep.meatlib.item;

import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class BaseSwordItem extends SwordItem implements MeatlibItem
{

    public BaseSwordItem(RegistrationContext ctx, ToolMaterial material, int attackDamage, float speed, Settings settings)
    {
        super(material, attackDamage, speed, settings);
    }
}
