package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.implant.item.ItemImplantInstaller;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A mundane item that conveniently stores an externally registered implant.
 * Useful if you want to make an item that represents a specific implant type.
 * This must be registered under {@link ItemImplantInstaller} elsewhere.
 * A JSON recipe is required for the referenced implant to be installed on an entity.
 */
public class ItemImplantItem extends BaseItem implements ItemImplantInstaller
{
    protected final Identifier implantId;

    public ItemImplantItem(RegistrationContext ctx, String registryName, int lines, Identifier implantId, Settings settings)
    {
        super(new ImplantTooltipSupplier(ctx.namespace(), registryName, lines), settings);
        this.implantId = implantId;
    }

    @Override
    public void install(ItemStack stack)
    {
        NMComponents.IMPLANT_MANAGER.maybeGet(stack).ifPresent(manager ->
        {
            manager.installImplant(implantId);
        });
    }

    public static class ImplantTooltipSupplier implements TooltipSupplier
    {
        private final String namespace;
        private final String name;
        private final int lines;

        public ImplantTooltipSupplier(String namespace, String name, int lines)
        {
            this.namespace = namespace;
            this.name = name;
            this.lines = lines;
        }

        @Override
        public void apply(Item item, List<Text> tooltip)
        {
            tooltip.add(Text.translatable("message." + namespace + ".item_implant.title").formatted(Formatting.BOLD).formatted(Formatting.GOLD));
            for (int i = 0; i < lines; ++i)
            {
                var txt = Text.translatable("implant." + namespace + "." + name + ".desc_" + i).formatted(Formatting.GOLD);
                TooltipSupplier.wrapLines(tooltip, txt);
            }
        }
    }
}
