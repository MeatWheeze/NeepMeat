package com.neep.neepmeat.plc.instruction.gui;

import com.google.common.collect.Maps;
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

    private static void register(InstructionProvider provider, Category category)
    {
        register(provider, new InstructionTooltip()
                .tooltip(Text.translatable(Instructions.REGISTRY.getId(provider).toTranslationKey("instruction") + ".desc"))
                .category(category));
    }

    public static InstructionTooltip get(InstructionProvider provider)
    {
        return TOOLTIPS.getOrDefault(provider, InstructionTooltip.EMPTY);
    }

    public static void init()
    {
        register(Instructions.END, Category.CONTROL);
        register(Instructions.RESTART, Category.COMPARISON);
        register(Instructions.RET, Category.CONTROL);
        register(Instructions.CALL, Category.CONTROL);
        register(Instructions.PUSH, Category.VARIABLE);
        register(Instructions.POP, Category.VARIABLE);
        register(Instructions.DUP, Category.VARIABLE);
        register(Instructions.DELAY, Category.CONTROL);
        register(Instructions.EQ, Category.COMPARISON);
        register(Instructions.LT, Category.COMPARISON);
        register(Instructions.LTEQ, Category.COMPARISON);
        register(Instructions.GT, Category.COMPARISON);
        register(Instructions.GTEQ, Category.COMPARISON);
        register(Instructions.INC, Category.ARITHMETIC);
        register(Instructions.DEC, Category.ARITHMETIC);
        register(Instructions.ADD, Category.ARITHMETIC);
        register(Instructions.SUB, Category.ARITHMETIC);
        register(Instructions.MUL, Category.ARITHMETIC);
        register(Instructions.DIV, Category.ARITHMETIC);
        register(Instructions.JUMP, Category.CONTROL);
        register(Instructions.BIT, Category.CONTROL);
        register(Instructions.BIF, Category.CONTROL);
        register(Instructions.SAY, Category.MISC);
        register(Instructions.REMOVE, Category.MANUFACTURE);
        register(Instructions.ROBOT, Category.CONTROL);
        register(Instructions.EXEC, Category.CONTROL);
        register(Instructions.COMBINE, Category.MANUFACTURE);
        register(Instructions.MOVE, Category.MANUFACTURE);
        register(Instructions.IMPLANT, Category.MANUFACTURE);
        register(Instructions.INJECT, Category.MANUFACTURE);
        register(Instructions.WAIT_REDSTONE, Category.REDSTONE);
        register(Instructions.EMIT_REDSTONE, Category.REDSTONE);
        register(Instructions.READ_REDSTONE, Category.REDSTONE);
    }

    public static class InstructionTooltip
    {
        private Category category = Category.MISC;
        private Text description = Text.empty();

        public InstructionTooltip()
        {

        }

        public InstructionTooltip tooltip(Text text)
        {
            this.description = text;
            return this;
        }

        public InstructionTooltip category(Category category)
        {
            this.category = category;
            return this;
        }

        public Category category()
        {
            return category;
        }

        public Text description()
        {
            return description;
        }

        public static final InstructionTooltip EMPTY = new InstructionTooltip();
    }

    public enum Category
    {
        ARITHMETIC(Text.translatable("category.neepmeat.instruction.arithmetic")),
        MANUFACTURE(Text.translatable("category.neepmeat.instruction.manufacture")),
        COMPARISON(Text.translatable("category.neepmeat.instruction.comparison")),
        CONTROL(Text.translatable("category.neepmeat.instruction.control")),
        VARIABLE(Text.translatable("category.neepmeat.instruction.variable")),
        REDSTONE(Text.translatable("category.neepmeat.instruction.redstone")),
        MISC(Text.translatable("category.neepmeat.instruction.misc"));

        public final Text name;

        Category(Text name)
        {
            this.name = name;
        }
    }
}
