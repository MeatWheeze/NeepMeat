package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("UnstableApiUsage")
public class ItemInPipe
{
    public double x;
    public double y;
    public double z;

    public Direction in;
    public Direction out;
    public float progress;
    public int pipeTicks;
    public long tickStart;
    public long tickEnd;
    public float speed;

    protected ItemVariant variant;
    protected int amount;

    public ItemInPipe(ResourceAmount<ItemVariant> amount, long tickStart)
    {
        this.in = null;
        this.out = null;
        this.progress = 0;
        this.variant = amount.resource();
        this.amount = (int) amount.amount();
//        this.itemStack = amount.resource().toStack((int) amount.amount());
        this.speed = 0.1f;
        this.tickStart = tickStart;
        this.tickEnd = (long) (tickStart + 1 / speed);
    }

    public ItemInPipe(ItemStack itemStack, long tickStart)
    {
        this(null, null, itemStack, tickStart);
    }

    public ItemInPipe(Direction in, Direction out, ItemStack itemStack, long tickStart)
    {
        this(in, out, ItemVariant.of(itemStack), itemStack.getCount(), tickStart);
    }

    public ItemInPipe(Direction in, Direction out, ItemVariant variant, int amount, long tickStart)
    {
        this.in = in;
        this.out = out;
        this.progress = 0;
        this.variant = variant;
        this.amount = amount;
        this.speed = 0.1f;
        this.tickStart = tickStart;
        this.tickEnd = (long) (tickStart + 1 / speed);
    }

    public static Vec3d directionUnit(Direction direction)
    {
        return new  Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public ItemStack getItemStack()
    {
        return variant.toStack(amount);
    }

    public int getAmount()
    {
        return amount;
    }

    public void set(Vec3d vec)
    {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void tick()
    {
        ++pipeTicks;
        progress = (pipeTicks) * speed;
    }

    public Vec3d getPosition(float prog)
    {
        float inFactor = 1 - prog;
        Vec3d vec;
        if (prog <= 0.5)
        {
            vec = directionUnit(in).multiply(inFactor - 0.5);
        }
        else
        {
            vec = directionUnit(out).multiply(prog - 0.5);
        }
        return vec;
    }

    public void reset(Direction in, Direction out, long tickStart)
    {
        this.progress = 0;
        this.pipeTicks = 0;
        this.in = in;
        this.out = out;
        this.tickStart = tickStart;
        this.tickEnd = (long) (tickStart + 1 / speed);
        this.set(new Vec3d(0, 0, 0));
    }

    public ResourceAmount<ItemVariant> toResourceAmount()
    {
        ResourceAmount<ItemVariant> itemVariantResourceAmount = new ResourceAmount<>(variant, amount);
        if (itemVariantResourceAmount.resource().isBlank())
        {
            itemVariantResourceAmount = new ResourceAmount<>(ItemVariant.of(Items.STONE.getDefaultStack()), 1);
        }
        return itemVariantResourceAmount;
//        return new ResourceAmount<ItemVariant>(ItemVariant.of(itemStack), itemStack.getCount());
    }

    public void decrement(int i)
    {
        this.amount -= i;
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        nbt.putInt("in", in.getId());
        nbt.putInt("out", out.getId());
        nbt.putLong("tick_start", tickStart);
        nbt.putLong("tick_end", tickEnd);

        NbtCompound item = new NbtCompound();
//        itemStack.writeNbt(item);
        nbt.put("variant", variant.toNbt());
        nbt.putInt("amount", amount);
        nbt.put("item", item);

        return nbt;
    }

    public static ItemInPipe fromNbt(NbtCompound nbt)
    {
        Direction in = Direction.byId(nbt.getInt("in"));
        Direction out = Direction.byId(nbt.getInt("out"));
        ItemVariant variant = ItemVariant.fromNbt(nbt.getCompound("variant"));
        int amount = nbt.getInt("amount");
        long tickStart = nbt.getLong("tick_start");

        ItemInPipe item = new ItemInPipe(in, out, variant, amount, tickStart);

        return item;
    }

    public ItemInPipe copyWith(int amount)
    {
        ItemStack newStack = getItemStack().copy();
        newStack.setCount(amount);
        return new ItemInPipe(in, out, newStack, tickStart);
    }
}
