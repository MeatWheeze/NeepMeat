package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class FluidIngredient extends GenericIngredient<Fluid, FluidVariant>
{
    public static final FluidIngredient EMPTY = new FluidIngredient(FluidVariant.blank(), 0);

    public FluidIngredient(@NotNull FluidVariant fluid, long amount)
    {
        super(fluid, amount);
    }

    public FluidIngredient(@NotNull Fluid fluid, long amount)
    {
        super(FluidVariant.of(fluid), amount);
    }

    @Override
    public String toString()
    {
        return "FluidIngredient{" + resource().getObject() + "}";
    }

    @Override
    public GenericIngredient<Fluid, FluidVariant> blank()
    {
        return EMPTY;
    }

    @Override
    public void write(PacketByteBuf buf)
    {
        if (this.isBlank())
        {
            buf.writeBoolean(false);
        }
        else
        {
            buf.writeBoolean(true);
            Fluid fluid = this.resource.getFluid();
            buf.writeVarInt(Registry.FLUID.getRawId(fluid)); // Fluid ID
            buf.writeLong(this.amount()); // Amount

            NbtCompound nbtCompound = this.resource().copyNbt();
            buf.writeNbt(nbtCompound);
        }
    }

    public static FluidIngredient fromPacket(PacketByteBuf buf)
    {
        if (!buf.readBoolean())
        {
            return EMPTY;
        }
        int i = buf.readVarInt(); // Fluid ID
        Fluid fluid = Registry.FLUID.get(i);
        long amount = buf.readLong(); // Amount
        NbtCompound nbt = buf.readNbt(); // NBT

        return new FluidIngredient(FluidVariant.of(fluid, nbt), amount);
    }

    public static FluidIngredient fromJson(JsonObject json)
    {
        if (json.size() < 2)
        {
            throw new JsonSyntaxException("Both resource and amount must be defined");
        }

        String string = JsonHelper.getString(json, "resource");
        long amount = JsonHelper.getLong(json, "amount");
        Identifier id = new Identifier(string);
        Fluid fluid = Registry.FLUID.get(id);
        return new FluidIngredient(fluid, amount);
    }
}
