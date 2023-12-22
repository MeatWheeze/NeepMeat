package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.OreFatRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class OreFatFluidVariantRenderHandler implements FluidVariantRenderHandler
{
    @Override
    public void appendTooltip(FluidVariant fluidVariant, List<Text> tooltip, TooltipContext tooltipContext)
    {
        NbtCompound nbt = fluidVariant.copyNbt();
        if (nbt != null)
        {
            tooltip.add(new TranslatableText("UwU").formatted(Formatting.AQUA));
        }
    }

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos)
    {
        NbtCompound nbt = fluidVariant.copyNbt();
        if (nbt != null)
        {
            Identifier itemId = new Identifier(nbt.getString("item"));
            Item item = Registry.ITEM.get(itemId);
            OreFatRegistry.Entry entry;
            if ((entry = OreFatRegistry.get(item)) != null)
            {
                return entry.col();
            }
        }
        return -1;

    }

    @Override
    @Nullable
    public Sprite[] getSprites(FluidVariant fluidVariant)
    {
        // Use the fluid render handler by default.
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidVariant.getFluid());

        if (fluidRenderHandler != null) {
            return fluidRenderHandler.getFluidSprites(null, null, fluidVariant.getFluid().getDefaultState());
        } else {
            return null;
        }
    }
}
