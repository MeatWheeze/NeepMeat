package com.neep.meatlib.api.entity;

import net.minecraft.entity.Entity;

public interface MultiPartEntity<T extends Entity>
{
    Iterable<T> getParts();
}
