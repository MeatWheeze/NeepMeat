package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import net.minecraft.nbt.NbtCompound;
import org.joml.Matrix4f;

import java.util.List;

public class BaseStaffModule extends AbstractMeatgunModule
{
    public BaseStaffModule(RootModuleHolder.Listener listener)
    {
        super(listener);

        var mainSlot = new SimpleModuleSlot(listener, new Matrix4f()
                .rotateX((float) Math.toRadians(90))
                .translate(0, 0, -8 / 16f));

        var auxSlot = new SimpleModuleSlot(listener, new Matrix4f()
                .rotateX((float) Math.toRadians(90))
                .rotateY((float) Math.toRadians(-90))
                .rotateZ((float) Math.toRadians(90))
                .translate(0, 4 / 16f, -1 / 16f));

        setSlots(List.of(mainSlot, auxSlot));
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE_STAFF;
    }

    public static BaseStaffModule fromNbt(RootModuleHolder.Listener listener, NbtCompound nbtCompound)
    {
        return new BaseStaffModule(listener);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }
}
