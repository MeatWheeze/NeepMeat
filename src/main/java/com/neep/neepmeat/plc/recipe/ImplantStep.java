package com.neep.neepmeat.plc.recipe;

import com.google.gson.JsonObject;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class ImplantStep implements ManufactureStep<Entity>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "implant");
    private final Item item;

    public ImplantStep(Item item)
    {
        this.item = item;
    }

    public ImplantStep(NbtCompound nbt)
    {
        Identifier id = Identifier.tryParse(nbt.getString("id"));
        if (id != null)
            item = Registry.ITEM.get(id);
        else
            item = Items.AIR;
    }

    public ImplantStep(JsonObject jsonObject)
    {
        String idString = JsonHelper.getString(jsonObject, "resource");
        Identifier id = Identifier.tryParse(idString);
        this.item = Registry.ITEM.get(id);
    }

    @Override
    public void mutate(Entity entity)
    {
        NMComponents.WORKPIECE.maybeGet(entity).ifPresent(workpiece ->
        {
            workpiece.addStep(this);
        });
    }

    @Override
    public Identifier getId()
    {
        return ID;
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
        tooltips.add(Text.literal("   ").append(item.getName()));
    }

    @Override
    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", Registry.ITEM.getId(item).toString());
        return nbt;
    }

    @Override
    public boolean equalsOther(ManufactureStep<?> o)
    {
        if (o instanceof ImplantStep other)
        {
            return other.item == item;
        }
        return false;
    }

    public ItemConvertible getItem()
    {
        return item;
    }
}
