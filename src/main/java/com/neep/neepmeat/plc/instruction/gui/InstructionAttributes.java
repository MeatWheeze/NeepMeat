package com.neep.neepmeat.plc.instruction.gui;

import com.google.common.collect.Maps;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.text.Text;

import java.util.Map;

public class InstructionAttributes
{
    private static final Map<InstructionProvider, InstructionTooltip> TOOLTIPS = Maps.newHashMap();

    public static void register(InstructionProvider provider, InstructionTooltip tooltip)
    {
        TOOLTIPS.put(provider, tooltip);
    }

    public static InstructionTooltip get(InstructionProvider provider)
    {
        return TOOLTIPS.getOrDefault(provider, InstructionTooltip.EMPTY);
    }

    public static void init()
    {
        register(Instructions.END, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".end.desc")));
        register(Instructions.GOTO_START, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".restart.desc")));
        register(Instructions.REMOVE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".remove.desc")));
        register(Instructions.COMBINE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".combine.desc")));
        register(Instructions.MOVE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".move.desc")));
        register(Instructions.IMPLANT, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".implant.desc")));
        register(Instructions.INJECT, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".inject.desc")));
        register(Instructions.WAIT_REDSTONE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".wait_redstone.desc")));
    }

    public record InstructionTooltip(Text description)
    {
        public static final InstructionTooltip EMPTY = new InstructionTooltip(Text.empty());
    }
}
