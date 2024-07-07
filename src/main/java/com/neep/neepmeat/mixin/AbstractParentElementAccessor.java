package com.neep.neepmeat.mixin;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractParentElement.class)
public interface AbstractParentElementAccessor
{
    @Accessor(value = "focused")
    @Nullable Element getFieldFocused();

    @Accessor(value = "focused")
    void setFieldFocused(@Nullable Element focused);
}
