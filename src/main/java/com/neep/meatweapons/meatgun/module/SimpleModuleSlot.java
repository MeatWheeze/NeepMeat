package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SimpleModuleSlot implements ModuleSlot
{
    protected final Matrix4f matrix;
    private final MeatgunComponent.Listener listener;
    private MeatgunModule module = MeatgunModule.DEFAULT;

    public SimpleModuleSlot(MeatgunComponent.Listener listener, Matrix4f matrix)
    {
        this.listener = listener;
        this.matrix = new Matrix4f(matrix);
    }

    @Override
    public @NotNull MeatgunModule get()
    {
        return module;
    }

    @Override
    public void set(MeatgunModule module)
    {
        this.module = module;
        module.setTransform(new Matrix4f(matrix));
        listener.markDirty();
    }

    @Override
    public Matrix4f transform()
    {
        return new Matrix4f(matrix);
    }

    @Override
    public Matrix4f transform(float tickDelta)
    {
        return new Matrix4f(matrix);
    }

    public Matrix4f transformStack()
    {
        return null;
    }

}
