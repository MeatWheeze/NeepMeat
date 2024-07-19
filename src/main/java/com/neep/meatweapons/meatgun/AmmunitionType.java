package com.neep.meatweapons.meatgun;

public enum AmmunitionType
{
    ENERGY("ENERG"),
    BALLISTIC("BLSTC"),
    BLOOD("BLOOD");

    private String string;

    private AmmunitionType(String string)
    {
        this.string = string;
    }

    public String stortName()
    {
        return string;
    }
}
