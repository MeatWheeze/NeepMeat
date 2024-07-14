package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;
import org.joml.Matrix4f;

import java.util.List;

public class BasePistolModule extends AbstractMeatgunModule
{
//    private MeatgunModule child = new ChuggerModule();
//    private MeatgunModule child = new TripleCarouselModule();
//    private final MeatgunModule child = new DoubleCarouselModule();
//    private final MeatgunModule child = new BosherModule();
//    private final MeatgunModule child = new BatteryModule();
//    private final MeatgunModule child = new LongBoiModule();

    private final ModuleSlot front;

    public BasePistolModule(MeatgunComponent.Listener listener)
    {
        super(listener);
        front = new SimpleModuleSlot(this.listener, new Matrix4f());

        setSlots(List.of(front));
        MeatgunModule child = new BosherModule(listener);
        front.set(child);
    }

    public BasePistolModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE_PISTOL;
    }

    public static BasePistolModule fromNbt(MeatgunComponent.Listener listener, NbtCompound nt)
    {
        return new BasePistolModule(listener, nt);
    }
}
