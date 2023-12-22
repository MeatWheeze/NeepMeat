package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;

public class CombineStep implements ManufactureStep<ItemStack>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "combine");

    private final Item item;

    public CombineStep(ItemStack stack)
    {
//        this.item = Registry.ITEM.getId(stack.getItem());
        this.item = stack.getItem();
    }

    public CombineStep(NbtCompound nbt)
    {
        Identifier id = Identifier.tryParse(nbt.getString("id"));
//        this.item = Objects.requireNonNullElseGet(id, () -> new Identifier("air"));
        if (id != null)
        {
            item = Registry.ITEM.get(id);
        }
        else
            item = Items.AIR;
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
    public void appendText(List<Text> tooltips)
    {
        tooltips.add(Text.translatable(ID.toTranslationKey("step")).formatted(Formatting.UNDERLINE));
        tooltips.add(Text.literal("   ").append(item.getName()));
    }

    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", Registry.ITEM.getId(item).toString());
        return nbt;
    }

    @Override
    public Identifier getId()
    {
        return ID;
    }
}
