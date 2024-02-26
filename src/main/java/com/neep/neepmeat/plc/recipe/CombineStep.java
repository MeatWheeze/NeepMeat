package com.neep.neepmeat.plc.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.WeakHashMap;

public class CombineStep implements ManufactureStep<ItemStack>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "combine");

    // Instance cache to save memory
    private static final WeakHashMap<ItemVariant, CombineStep> CACHE = new WeakHashMap<>();

    synchronized public static CombineStep get(ItemStack stack)
    {
        return CACHE.computeIfAbsent(ItemVariant.of(stack), CombineStep::new);
    }

    synchronized public static CombineStep get(NbtCompound nbt)
    {
        ItemVariant item = ItemVariant.fromNbt(nbt.getCompound("variant"));
        return CACHE.computeIfAbsent(item, CombineStep::new);
    }

    synchronized public static CombineStep get(JsonObject jsonObject)
    {
        String idString = JsonHelper.getString(jsonObject, "resource");
        Identifier id = Identifier.tryParse(idString);
        var item = Registries.ITEM.get(id);

        if (item == Items.AIR)
            throw new JsonParseException("Unknown item " + id);

        return CACHE.computeIfAbsent(ItemVariant.of(item), CombineStep::new);
    }

    private final ItemVariant item;

    public CombineStep(ItemVariant variant)
    {
        this.item = variant;
    }

    @Override
    public void mutate(ItemStack stack)
    {
        NMComponents.WORKPIECE.maybeGet(stack).ifPresent(workpiece ->
        {
            workpiece.addStep(this);
        });
    }

    @Override
    public Text getName()
    {
        return Text.translatable(ID.toTranslationKey("step")).formatted(Formatting.UNDERLINE);
    }

    @Override
    public void appendText(List<Text> tooltips)
    {
        tooltips.add(Text.translatable(ID.toTranslationKey("step")).formatted(Formatting.UNDERLINE));
        tooltips.add(Text.literal("   ").append(item.getObject().getName()));
    }

    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.put("variant", item.toNbt());
        return nbt;
    }

    @Override
    public boolean equalsOther(ManufactureStep<?> o)
    {
        if (o instanceof CombineStep other)
        {
            return other.item.equals(item);
        }
        return false;
    }

    @Override
    public Identifier getId()
    {
        return ID;
    }

    public Item getItem()
    {
        return item.getItem();
    }

    public ItemVariant getVariant()
    {
        return item;
    }
}
