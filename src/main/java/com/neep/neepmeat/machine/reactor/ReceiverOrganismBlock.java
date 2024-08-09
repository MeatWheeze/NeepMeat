package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.PropertyValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ReceiverOrganismBlock extends BaseBlock implements ReceiverOrganismStructure
{
    private final EnumMap<Property, PropertyValue> properties;

    public ReceiverOrganismBlock(RegistrationContext ctx, Map<Property, PropertyValue> properties, Settings settings)
    {
        super(ctx, ItemSettings.block().factory(ReceiverOrganismBlockItem::new), settings);
        this.properties = new EnumMap<>(properties);
    }

    @Override
    public EnumMap<Property, PropertyValue> getProperties()
    {
        return properties;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
    {
        super.appendTooltip(stack, world, tooltip, options);

        if (!Screen.hasShiftDown())
        {
            tooltip.add(NeepMeat.translationKey("screen", "receiver_organism.block_hold_shift").formatted(Formatting.RED));
        }
        else
        {
            tooltip.add(NeepMeat.translationKey("screen", "receiver_organism.block").formatted(Formatting.RED));
            for (var entry : properties.entrySet())
            {
                MutableText base = Text.literal("   ")
                        .append(NeepMeat.translationKey("screen", "receiver_organism." + entry.getKey().name().toLowerCase())
                                .formatted(Formatting.GOLD))
                        .append(Text.literal(": ").formatted(Formatting.GOLD))
                        .append(entry.getKey().format(entry.getValue()))
                        .append(Text.literal(" (" + entry.getValue().function().name + ")"))
                        ;
                tooltip.add(base);
            }
        }
    }

    public static class ReceiverOrganismBlockItem extends BaseBlockItem
    {
        public ReceiverOrganismBlockItem(Block block, RegistrationContext ctx, ItemSettings itemSettings)
        {
            super(block, ctx, itemSettings);
        }

        @Override
        public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
        {
            super.appendTooltip(itemStack, world, tooltip, tooltipContext);
            getBlock().appendTooltip(itemStack, world, tooltip, tooltipContext);
        }
    }
}
