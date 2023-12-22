package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PLCRecipes
{
    public static final ManufactureStep.Provider<?> COMBINE = ManufactureStep.register(CombineStep.ID, CombineStep::new);

    public static void init()
    {
        ItemTooltipCallback.EVENT.register((stack, context, lines) ->
        {
            NMComponents.WORKPIECE.maybeGet(stack).ifPresent(workpiece ->
            {
                var steps = workpiece.getSteps();
                if (!steps.isEmpty())
                {
                    lines.add(Text.translatable("message.neepmeat.workpiece.title").formatted(Formatting.GOLD, Formatting.BOLD));
                    for (var entry : steps)
                    {
                        entry.appendText(lines);
                    }
                }
            });
        });
    }
}
