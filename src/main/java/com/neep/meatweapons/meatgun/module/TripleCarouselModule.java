package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;

public class TripleCarouselModule extends AbstractMeatgunModule
{
    public float lerpAngle;

    private int rotateTicks = 5;
    private int selected = 0;
    private long rotateStartTime = 0;

    public TripleCarouselModule(RootModuleHolder.Listener listener)
    {
        super(listener);
        var slot0 = new TCSlot(this.listener, 0);
        var slot1 = new TCSlot(this.listener, 1);
        var slot2 = new TCSlot(this.listener, 2);

        setSlots(List.of(slot0, slot1, slot2));
    }

    public TripleCarouselModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        if (!isRotating(world.getTime()))
        {
            if (id == 2)
            {
                rotate(player);
            }
            else
            {
                slots.get(selected).get().trigger(world, player, stack, id, pitch, yaw, handType, hand);
                rotate(player);
            }
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        if (!isRotating(world.getTime()))
        {
            if (id == 2)
            {
                rotate(player);
            }
            else
            {
                slots.get(selected).get().tickTrigger(world, player, stack, id, pitch, yaw, handType, hand);
                rotate(player);
            }
        }
    }

    private void rotate(PlayerEntity player)
    {
        rotateStartTime = player.getWorld().getTime();
        sync(player);
    }

    public boolean isRotating(long time)
    {
        return time < rotateStartTime + rotateTicks;
    }

    @Override
    public void tick(PlayerEntity player)
    {
        super.tick(player);
        long time = player.getWorld().getTime();

        if (!player.getWorld().isClient())
        {
            if (isRotating(time))
            {
            }
            else if (time == rotateStartTime + rotateTicks)
            {
                rotateStartTime = 0;
                selected = (selected + 1) % 3;
                slots.forEach(s -> s.get().setTransform(s.transform()));
                sync(player);
            }
        }
        else if (time == rotateStartTime + rotateTicks)
        {
            selected = (selected + 1) % 3;
        }
    }

    void sync(PlayerEntity player)
    {

//        PacketByteBuf buf = listener.getBuf(this);
//        buf.writeLong(rotateStartTime);
//        buf.writeInt(selected);
//        listener.send(player, buf);
        listener.markDirty(RootModuleHolder.Reason.SAVE_DATA);
    }

    @Override
    public void receivePacket(PacketByteBuf buf)
    {
        this.rotateStartTime = buf.readLong();
        this.selected = buf.readInt();
    }

    public float getRotation(long time, float tickDelta)
    {
        float thing = rotateTicks - (rotateStartTime + rotateTicks - time);
        if (thing > rotateTicks)
            return 0;

        return MathHelper.clamp((thing + tickDelta) / rotateTicks, 0, 1);
    }

    public int getSelected()
    {
        return selected;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
//        nbt.putInt("rotate_ticks", rotateTicks);
        nbt.putInt("selected", selected);
        nbt.putLong("rotate_start_time", rotateStartTime);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
//        this.rotateTicks = nbt.getInt("rotate_ticks");
        this.selected = nbt.getInt("selected");
        this.rotateStartTime = nbt.getLong("rotate_start_time");
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.TRIPLE_CAROUSEL;
    }

    private class TCSlot extends SimpleModuleSlot
    {
        private final int index;
        public TCSlot(RootModuleHolder.Listener listener, int index)
        {
            super(listener,
                new Matrix4f().rotateZ(index * 2f / 3f * MathHelper.PI).translate(0, 4 / 16f, -2 / 16f));
            this.index = index;
        }

        @Override
        public Matrix4f transform()
        {
            return new Matrix4f()
                    .rotateZ((index - selected) * 2f / 3f * MathHelper.PI)
                    .translate(0, 4 / 16f, -2 / 16f);
        }

        @Override
        public Matrix4f transform(float tickDelta)
        {
            return super.transform(tickDelta);
        }
    }
}
