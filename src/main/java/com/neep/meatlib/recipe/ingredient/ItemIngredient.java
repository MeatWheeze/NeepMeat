package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class ItemIngredient extends GenericIngredient<Item, ItemVariant>
{
    public static final ItemIngredient EMPTY = new ItemIngredient(ItemVariant.blank(), 0);

    public ItemIngredient(@NotNull ItemVariant item, long amount)
    {
        super(item, amount);
    }

    public ItemIngredient(@NotNull Item item, long amount)
    {
        super(ItemVariant.of(item), amount);
    }

    @Override
    public GenericIngredient<Item, ItemVariant> blank()
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
            Item item = this.resource.getItem();
            buf.writeVarInt(Registries.ITEM.getRawId(item)); // ID
            buf.writeLong(this.amount()); // Amount

            NbtCompound nbtCompound = this.resource().copyNbt();
            buf.writeNbt(nbtCompound);
        }
    }

    public static ItemIngredient fromBuffer(PacketByteBuf buf)
    {
        if (!buf.readBoolean())
        {
            return EMPTY;
        }
        int i = buf.readVarInt(); // Fluid ID
        Item item = Registries.ITEM.get(i);
        long amount = buf.readLong(); // Amount
        NbtCompound nbt = buf.readNbt(); // NBT

        return new ItemIngredient(ItemVariant.of(item, nbt), amount);
    }

    public static ItemIngredient fromJson(JsonObject json)
    {
        if (json.size() < 2)
        {
            throw new JsonSyntaxException("Both resource and amount must be defined");
        }

        String string = JsonHelper.getString(json, "resource");
        long amount = JsonHelper.getLong(json, "amount");
        Identifier id = new Identifier(string);
        Item item = Registries.ITEM.get(id);
        return new ItemIngredient(item, amount);
    }
}
