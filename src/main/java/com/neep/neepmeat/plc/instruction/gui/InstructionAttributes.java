package com.neep.neepmeat.plc.instruction.gui;

import com.google.common.collect.Maps;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Instruction;
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

    private static void register(InstructionProvider provider)
    {
        register(provider, new InstructionTooltip(Text.translatable(Instructions.REGISTRY.getId(provider).toTranslationKey("instruction") + ".desc")));
    }

    public static InstructionTooltip get(InstructionProvider provider)
    {
        return TOOLTIPS.getOrDefault(provider, InstructionTooltip.EMPTY);
    }

    public static void init()
    {
        register(Instructions.END, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".end.desc")));
        register(Instructions.RESTART, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".restart.desc")));
        register(Instructions.RET, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".ret.desc")));
        register(Instructions.CALL, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".call.desc")));
        register(Instructions.PUSH);
        register(Instructions.POP);
        register(Instructions.DELAY);
        register(Instructions.EQ);
        register(Instructions.LT);
        register(Instructions.LTEQ);
        register(Instructions.GT);
        register(Instructions.GTEQ);
        register(Instructions.INC);
        register(Instructions.DEC);
        register(Instructions.ADD);
        register(Instructions.SUB);
        register(Instructions.MUL);
        register(Instructions.DIV);
        register(Instructions.JUMP);
        register(Instructions.BIT);
        register(Instructions.SAY);
        register(Instructions.SAY);
        register(Instructions.REMOVE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".remove.desc")));
        register(Instructions.ROBOT);
        register(Instructions.EXEC);
        register(Instructions.COMBINE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".combine.desc")));
        register(Instructions.MOVE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".move.desc")));
        register(Instructions.IMPLANT, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".implant.desc")));
        register(Instructions.INJECT, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".inject.desc")));
        register(Instructions.WAIT_REDSTONE, new InstructionTooltip(Text.translatable("instruction." + NeepMeat.NAMESPACE + ".wait_redstone.desc")));
        register(Instructions.EMIT_REDSTONE);
    }

    public record InstructionTooltip(Text description)
    {
        public static final InstructionTooltip EMPTY = new InstructionTooltip(Text.empty());
    }
}
