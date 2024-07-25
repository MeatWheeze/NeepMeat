package com.neep.meatweapons.meatgun.module;

import java.util.EnumMap;

public class AttributeContainer
{
    public final EnumMap<MeatgunModule.Attribute, Float> map = new EnumMap<>(MeatgunModule.Attribute.class);

    public AttributeContainer add(MeatgunModule.Attribute attribute, float value)
    {
        map.put(attribute, value);
        return this;
    }

    public AttributeContainer complexity(int value)
    {
        map.put(MeatgunModule.Attribute.COMPLEXITY, (float) value);
        return this;
    }

    public AttributeContainer cooldown(int value)
    {
        map.put(MeatgunModule.Attribute.COOLDOWN, (float) value);
        return this;
    }

    public AttributeContainer attackDamage(float value)
    {
        map.put(MeatgunModule.Attribute.ATTACK_DAMAGE, (float) value);
        return this;
    }

    public AttributeContainer consume(float value)
    {
        map.put(MeatgunModule.Attribute.AMOUNT_PER_USE, value);
        return this;
    }

    public float get(MeatgunModule.Attribute attribute)
    {
        return (int) map.getOrDefault(attribute, 0f).floatValue();
    }

    public float attackDamage()
    {
        return get(MeatgunModule.Attribute.ATTACK_DAMAGE);
    }
}
