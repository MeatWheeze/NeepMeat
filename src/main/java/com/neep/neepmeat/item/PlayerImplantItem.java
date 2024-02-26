package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.implant.player.EntityImplantInstaller;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayerImplantItem extends BaseItem implements EntityImplantInstaller
{
    protected final Identifier implantId;

    public PlayerImplantItem(String registryName, int lines, Identifier implantId, Settings settings)
    {
        super(registryName, new ImplantTooltipSupplier(registryName, lines), settings);
        this.implantId = implantId;
        ItemRegistry.queue(this);
    }

    @Override
    public void install(Entity entity)
    {
        NMComponents.IMPLANT_MANAGER.maybeGet(entity).ifPresent(manager ->
        {
            manager.installImplant(implantId);
        });
    }

    public static class ImplantTooltipSupplier implements TooltipSupplier
    {
        private final String name;
        private final int lines;
        public ImplantTooltipSupplier(String name, int lines)
        {
            this.name = name;
            this.lines = lines;
        }

        @Override
        public void apply(Item item, List<Text> tooltip)
        {
            tooltip.add(Text.translatable("message." + NeepMeat.NAMESPACE + ".implant.title").formatted(Formatting.BOLD).formatted(Formatting.GOLD));
            for (int i = 0; i < lines; ++i)
            {
                var txt = Text.translatable("implant." + NeepMeat.NAMESPACE + "." + name + ".desc_" + i).formatted(Formatting.GOLD);
                TooltipSupplier.wrapLines(tooltip, txt);
            }
        }
    }
}
