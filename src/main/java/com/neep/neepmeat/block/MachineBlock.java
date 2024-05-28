package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;
import com.neep.neepmeat.api.live_machine.StructureProperty;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MachineBlock extends BaseBlock implements LivingMachineStructure
{
    private final EnumMap<StructureProperty, StructureProperty.Entry> properties;

    public MachineBlock(String registryName, ItemSettings itemSettings, Map<StructureProperty, StructureProperty.Entry> properties, Settings settings)
    {
        super(registryName, itemSettings.factory(MachineBlockItem::new), settings);
        this.properties = new EnumMap<>(properties);
//        properties.put(Property.MAX_POWER, 10f);
    }

    @Override
    public EnumMap<StructureProperty, StructureProperty.Entry> getProperties()
    {
        return properties;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
    {
        super.appendTooltip(stack, world, tooltip, options);

        if (!Screen.hasShiftDown())
        {
            tooltip.add(NeepMeat.translationKey("screen", "living_machine.block_hold_shift").formatted(Formatting.RED));
        }
        else
        {
            tooltip.add(NeepMeat.translationKey("screen", "living_machine.block").formatted(Formatting.RED));
            for (var entry : properties.entrySet())
            {
                MutableText base = Text.literal("   ")
                        .append(NeepMeat.translationKey("screen", "living_machine." + entry.getKey().name().toLowerCase())
                                .formatted(Formatting.GOLD))
                        .append(Text.literal(": ").formatted(Formatting.GOLD))
                        .append(entry.getKey().format(entry.getValue()))
                        .append(Text.literal(" (" + entry.getValue().function().name + ")"))
                        ;
                tooltip.add(base);
            }
        }
    }

    public static class MachineBlockItem extends BaseBlockItem
    {
        public MachineBlockItem(Block block, String registryName, ItemSettings itemSettings)
        {
            super(block, registryName, itemSettings);
        }

        @Override
        public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
        {
            super.appendTooltip(itemStack, world, tooltip, tooltipContext);
            getBlock().appendTooltip(itemStack, world, tooltip, tooltipContext);
        }
    }
}
