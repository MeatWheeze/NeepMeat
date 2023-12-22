package com.neep.neepmeat.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ItemInPipe
{
    public double x;
    public double y;
    public double z;

    public Direction in;
    public Direction out;
    public float progress;
    protected float delta;
    protected Vec3d deltaVec = new Vec3d(0, 0, 0);
    public float speed;

    protected ItemStack itemStack;

    public ItemInPipe(Direction in, Direction out, ItemStack itemStack)
    {
        this.in = in;
        this.out = out;
        this.progress = 0;
        this.itemStack = itemStack;
        this.speed = 0.1f;
    }

    public static Vec3d directionUnit(Direction direction)
    {
//        Vec3d vec = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
//        return vec.subtract(vec.multiply(0.5));
        return new  Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public void set(Vec3d vec)
    {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void step(float delta)
    {
        progress += speed;
//        update();
    }

    public Vec3d update(float prog)
    {
        float inFactor = 1 - prog;
        float outFactor = prog;
        Vec3d vec;
        if (prog <= 0.5)
        {
            vec = directionUnit(in).multiply(inFactor - 0.5);
        }
        else
        {
            vec = directionUnit(out).multiply(outFactor - 0.5);
        }
        return vec;
    }

    public Vec3d interpolate(float tickDelta)
    {
//        set(update(progress));
        Vec3d vec = update(progress).lerp(update(progress + speed), tickDelta);
//        return new Vec3d(x, y, z);
//        return new Vec3d(x + speed * tickDelta, y, z);
//        return new Vec3d(x + speed * tickDelta, y + speed * tickDelta, z + speed * tickDelta);

//        deltaVec = deltaVec.lerp(update(progress), 0.01);
        return vec;
    }

    public void reset(Direction in, Direction out)
    {
        this.progress = 0;
        this.in = in;
        this.out = out;
        this.set(new Vec3d(0, 0, 0));
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        nbt.putInt("in", in.getId());
        nbt.putInt("out", out.getId());
        nbt.putFloat("progress", progress);

        NbtCompound item = new NbtCompound();
        itemStack.writeNbt(item);
//        System.out.println(itemStack);
        nbt.put("item", item);

        return nbt;
    }

    public static ItemInPipe fromNbt(NbtCompound nbt)
    {
        Direction in = Direction.byId(nbt.getInt("in"));
        Direction out = Direction.byId(nbt.getInt("out"));
//        ItemStack stack = ItemStack.fromNbt(nbt.getCompound("item").getCompound("item"));
        ItemStack stack = ItemStack.fromNbt(nbt.getCompound("item"));

        ItemInPipe offset = new ItemInPipe(in, out, stack);
        offset.progress = nbt.getFloat("progress");

        return offset;
    }
}
