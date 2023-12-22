package com.neep.neepmeat.plc.recipe;

import com.google.gson.JsonObject;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class InjectStep implements ManufactureStep<ItemStack>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "inject");

    private final FluidVariant fluid;

    public InjectStep(FluidVariant fluid)
    {
        this.fluid = fluid;
    }

    public InjectStep(NbtCompound nbt)
    {
        fluid = FluidVariant.fromNbt(nbt.getCompound("fluid"));
    }

    public InjectStep(JsonObject jsonObject)
    {
        String idString = JsonHelper.getString(jsonObject, "resource");
        Identifier id = Identifier.tryParse(idString);
        this.fluid = FluidVariant.of(Registry.FLUID.get(id));
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
        tooltips.add(Text.literal("   ").append(FluidVariantAttributes.getName(fluid)));
    }

    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.put("fluid", fluid.toNbt());
        return nbt;
    }

    @Override
    public boolean equalsOther(ManufactureStep<?> o)
    {
        if (o instanceof InjectStep other)
        {
            return other.fluid == fluid;
        }
        return false;
    }

    @Override
    public Identifier getId()
    {
        return ID;
    }
}
