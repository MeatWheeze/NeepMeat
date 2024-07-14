package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;
import org.joml.Matrix4f;

import java.util.List;

public class BaseStaffModule extends AbstractMeatgunModule
{
    public BaseStaffModule(MeatgunComponent.Listener listener)
    {
        super(listener);

        var slot = new SimpleModuleSlot(listener, new Matrix4f()
                .rotateX((float) Math.toRadians(90))
                .translate(0, 0, -1));

        setSlots(List.of(slot));
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE_STAFF;
    }

    public static BaseStaffModule fromNbt(MeatgunComponent.Listener listener, NbtCompound nbtCompound)
    {
        return new BaseStaffModule(listener);
    }
}
