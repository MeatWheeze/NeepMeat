package com.neep.meatweapons.meatgun;

public enum AmmunitionType
{
    ENERGY("ENERG", 0.25f),
    BALLISTIC("BLSTC", 0.5f),
    BLOOD("BLOOD", 0.1f);

    private final String string;
    public final float healthPerUnit;

    AmmunitionType(String string, float healthPerUnit)
    {
        this.string = string;
        this.healthPerUnit = healthPerUnit;
    }

    public String shortName()
    {
        return string;
    }
}
