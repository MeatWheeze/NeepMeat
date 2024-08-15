package com.neep.neepbus.util;

public class SimpleEntry implements ConfigEntry
{
    private final String name;
    private String address;

    public SimpleEntry(String name)
    {
        this.name = name;
        this.address = name.toLowerCase();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getAddress()
    {
        return address;
    }

    @Override
    public void setAddress(String address)
    {
        this.address = address;
    }
}
