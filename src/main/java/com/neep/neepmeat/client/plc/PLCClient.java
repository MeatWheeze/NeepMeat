package com.neep.neepmeat.client.plc;

import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.recipe.ItemWorkpiece;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class PLCClient
{
    public static void init()
    {
        ItemTooltipCallback.EVENT.register((stack, context, lines) ->
        {
            if (ItemWorkpiece.has(stack))
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
            }
        });
    }
}
