package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;

public class BaseStaffModule extends AbstractMeatgunModule
{
    private final SimpleModuleSlot mainSlot;

    public BaseStaffModule(RootModuleHolder.Listener listener)
    {
        super(listener);

        mainSlot = new SimpleModuleSlot(listener, new Matrix4f()
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
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        mainSlot.get().trigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        mainSlot.get().release(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        mainSlot.get().tickTrigger(world, player, stack, id, pitch, yaw, handType);
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
