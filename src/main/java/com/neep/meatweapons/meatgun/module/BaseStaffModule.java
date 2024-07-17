package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.component.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;
import org.joml.Matrix4f;

import java.util.List;

public class BaseStaffModule extends AbstractMeatgunModule
{
    private long startTime = 0;

    public BaseStaffModule(MeatgunComponent.Listener listener)
    {
        super(listener);

        var mainSlot = new SimpleModuleSlot(listener, new Matrix4f()
                .rotateX((float) Math.toRadians(90))
                .translate(0, 0, -8 / 16f));

        var auxSlot = new SimpleModuleSlot(listener, new Matrix4f()
                .rotateX((float) Math.toRadians(90))
                .rotateY((float) Math.toRadians(-90))
                .translate(-3 / 16f, 0, -1 / 16f));

        setSlots(List.of(mainSlot, auxSlot));
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putLong("animation_start", startTime);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.startTime = nbt.getLong("animation_start");
    }

    public void animateTest(long time)
    {
        startTime = time;
        listener.markDirty();
    }

    public long getAnimationStart()
    {
        return startTime;
    }
}
