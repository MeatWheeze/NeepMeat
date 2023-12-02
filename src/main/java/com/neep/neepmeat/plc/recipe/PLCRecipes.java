package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

public class PLCRecipes
{
    public static final ManufactureStep.Provider<?> COMBINE = ManufactureStep.register(CombineStep.ID, CombineStep::new);

    public static void init()
    {
        ItemTooltipCallback.EVENT.register((stack, context, lines) ->
        {
            NMComponents.WORKPIECE.maybeGet(stack).ifPresent(workpiece ->
            {
                for (var entry : workpiece.getSteps())
                {
                    entry.appendText(lines);
//                    lines.add(Text.of(entry.getId().toString()));
                }
            });
        });
    }
}
