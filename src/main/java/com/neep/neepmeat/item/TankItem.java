package com.neep.neepmeat.item;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TankItem extends FluidComponentItem
{
    public TankItem(Block block, String registryName, int itemMaxStack, boolean hasLore)
    {
        super(block, registryName, itemMaxStack, hasLore);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        NbtCompound blockNbt = getBlockEntityNbt(itemStack);
        if (blockNbt == null) return;

        FluidVariant variant = FluidVariant.fromNbt(blockNbt.getCompound(WritableSingleFluidStorage.KEY_RESOURCE));
        if (!variant.isBlank())
        {
            long mb = Math.floorDiv(blockNbt.getLong(WritableSingleFluidStorage.KEY_AMOUNT), FluidConstants.BUCKET / 1000);
            int col = FluidVariantRendering.getColor(variant);
            MutableText text = FluidVariantAttributes.getName(variant).shallowCopy();
            text.append(": " + mb + "mb");
            text.setStyle(text.getStyle().withColor(col));
            tooltip.add(text);
        }
    }

    @Override
    public Text getName(ItemStack stack)
    {
        NbtCompound blockNbt = getBlockEntityNbt(stack);
        FluidVariant variant;
        if (blockNbt != null && !(variant = FluidVariant.fromNbt(blockNbt.getCompound(WritableSingleFluidStorage.KEY_RESOURCE))).isBlank())
        {
            return new TranslatableText(this.getTranslationKey() + ".filled", FluidVariantAttributes.getName(variant));
        }
        return super.getName(stack);
    }
}