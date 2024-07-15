package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;

public class UnderbarrelModule extends AbstractMeatgunModule
{
    private final ModuleSlot upSlot;
    private final ModuleSlot downSlot;

    public UnderbarrelModule(MeatgunComponent.Listener listener)
    {
        super(listener);
        upSlot = new SimpleModuleSlot(this.listener, new Matrix4f().translate(0, 0, -3 / 16f));
        downSlot = new SimpleModuleSlot(this.listener, new Matrix4f()
                .scale(0.999f, 1f, 1f)
                .translate(0, 0, -0.001f)
                .rotateZ(MathHelper.PI)
                .translate(0, 4 / 16f, -3 / 16f));
        setSlots(List.of(upSlot, downSlot));
    }

    public UnderbarrelModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
        readNbt(nbt);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 1)
            upSlot.get().trigger(world, player, stack, 1, pitch, yaw, handType);

        if (id == 2)
            downSlot.get().trigger(world, player, stack, 1, pitch, yaw, handType);
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 1)
            upSlot.get().tickTrigger(world, player, stack, 1, pitch, yaw, handType);

        if (id == 2)
            downSlot.get().tickTrigger(world, player, stack, 1, pitch, yaw, handType);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.UNDERBARREL;
    }
}
