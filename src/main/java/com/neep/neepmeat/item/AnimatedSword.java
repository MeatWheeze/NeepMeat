package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseSwordItem;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.network.ISyncable;

public abstract class AnimatedSword extends BaseSwordItem implements IAnimatable, ISyncable, OverrideSwingItem
{
    public static final int ANIM_SWING = 0;
    public static final int ANIM_STAB = 1;
    public static final int ANIM_CHOP = 2;

    public AnimatedSword(String registryName, ToolMaterial material, int attackDamage, float speed, Settings settings)
    {
        super(registryName, material, attackDamage, speed, settings);
    }

}
