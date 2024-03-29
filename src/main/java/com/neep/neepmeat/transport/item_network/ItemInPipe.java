package com.neep.neepmeat.transport.item_network;

import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    protected List<Direction> route = List.of();

    public ItemInPipe(ResourceAmount<ItemVariant> amount, long tickStart)
    {
        this(null, null, amount.resource(), (int) amount.amount(), tickStart);
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

    public ItemVariant resource()
    {
        return variant;
    }

    public long amount()
    {
        return Math.max(amount, 0);
    }

    public void setRoute(List<Direction> route)
    {
        this.route = route;
    }

    public Direction getPreferredOutputDirection(BlockState state, Direction in, ItemPipe pipe)
    {
        Set<Direction> options = pipe.getConnections(state, d -> d != in);
        if (options.size() > 1 && route != null && !route.isEmpty())
        {
            return route.remove(0);
        }
        return null;
    }

    public static Vec3d directionUnit(Direction direction)
    {
        return new  Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public ItemStack getItemStack()
    {
        return variant.toStack(amount);
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

    public static byte[] writeRoute(List<Direction> route)
    {
        byte[] bytes = new byte[route.size()];
        for (int i = 0; i < route.size(); ++i)
        {
            bytes[i] = (byte) route.get(i).getId();
        }
        return bytes;
    }

    public static List<Direction> readRoute(NbtByteArray byteArray)
    {
        if (byteArray == null) return null;
        return byteArray.stream().map(b -> Direction.byId(b.byteValue())).collect(Collectors.toList());
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        nbt.putInt("in", in.getId());
        nbt.putInt("out", out.getId());
        nbt.putLong("tick_start", tickStart);
        nbt.putLong("tick_end", tickEnd);

        NbtCompound item = new NbtCompound();
        nbt.put("variant", variant.toNbt());
        nbt.putInt("amount", amount);
        nbt.put("item", item);
        if (route != null)
            nbt.putByteArray("route", writeRoute(route));

        return nbt;
    }

    public static ItemInPipe fromNbt(NbtCompound nbt)
    {
        Direction in = Direction.byId(nbt.getInt("in"));
        Direction out = Direction.byId(nbt.getInt("out"));
        ItemVariant variant = ItemVariant.fromNbt(nbt.getCompound("variant"));
        int amount = nbt.getInt("amount");
        long tickStart = nbt.getLong("tick_start");
        List<Direction> route = readRoute((NbtByteArray) nbt.get("route"));

        ItemInPipe item = new ItemInPipe(in, out, variant, amount, tickStart);
        item.setRoute(route);

        return item;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public ItemInPipe copyWith(int amount)
    {
        ItemInPipe newItem = new ItemInPipe(in, out, variant, amount, tickStart);

        newItem.x = x;
        newItem.y = y;
        newItem.z = z;
        newItem.speed = speed;
        newItem.tickEnd = tickEnd;
        newItem.pipeTicks = pipeTicks;

        newItem.route = new ArrayList<>(route.size());
        newItem.route.addAll(route);

        return newItem;
    }

//    public ItemInPipe copyWith(int amount)
//    {
//        ItemInPipe newItem = new ItemInPipe(in, out, variant, amount, tickStart);
//        if (route != null)
//            newItem.setRoute((List<Direction>) route.clone());
//        return newItem;
//    }
}
