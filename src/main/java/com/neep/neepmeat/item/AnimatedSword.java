package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseSwordItem;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import com.neep.neepmeat.entity.AnimationSyncable;
import net.minecraft.item.ToolMaterial;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class AnimatedSword extends BaseSwordItem implements AnimationSyncable, OverrideSwingItem, GeoItem
{
    public static final int ANIM_SWING = 0;
    public static final int ANIM_STAB = 1;
    public static final int ANIM_CHOP = 2;

    public AnimatedSword(String registryName, ToolMaterial material, int attackDamage, float speed, Settings settings)
    {
        super(registryName, material, attackDamage, speed, settings);
    }

}
