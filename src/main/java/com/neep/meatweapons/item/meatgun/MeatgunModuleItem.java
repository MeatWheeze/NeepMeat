package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeatgunModuleItem extends Item
{
    private static final Map<MeatgunModuleItem, MeatgunModule.Type<?>> ITEM_TO_TYPE = new HashMap<>();
    private static final Map<MeatgunModule.Type<?>, MeatgunModuleItem> TYPE_TO_ITEM = new HashMap<>();
    private final MeatgunModule.Type<?> type;
    @Nullable
    private final TooltipSupplier tooltipSupplier;
    private String translationKey;

    public MeatgunModuleItem(MeatgunModule.Type<?> type, @Nullable TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(settings);
        this.type = type;
        this.tooltipSupplier = tooltipSupplier;
        register(this, type);
    }

    public MeatgunModuleItem(MeatgunModule.Type<?> type, Settings settings)
    {
        this(type, null, settings);
    }

    public static void register(MeatgunModuleItem item, MeatgunModule.Type<?> type)
    {
        if (TYPE_TO_ITEM.containsKey(type))
            throw new IllegalArgumentException("Module type '" + type + "' is already bound to item '" + TYPE_TO_ITEM.get(type) + "'");

        ITEM_TO_TYPE.put(item, type);
        TYPE_TO_ITEM.put(type, item);
    }

    public static ItemStack get(MeatgunModule.Type<?> type)
    {
        MeatgunModuleItem item = TYPE_TO_ITEM.get(type);
        if (item != null)
        {
            return item.getDefaultStack();
        }
        return ItemStack.EMPTY;
    }

    public static MeatgunModule.Type<?> get(ItemStack stack)
    {
        if (stack.getItem() instanceof MeatgunModuleItem moduleItem)
        {
            MeatgunModule.Type<?> type = ITEM_TO_TYPE.get(moduleItem);
            if (type != null)
            {
                return type;
            }
        }
        return MeatgunModule.DEFAULT_TYPE;
    }

    @Override
    protected String getOrCreateTranslationKey()
    {
        if (this.translationKey == null)
        {
            this.translationKey = Util.createTranslationKey("meatgun_module", Registries.ITEM.getId(this));
        }

        return this.translationKey;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getOrCreateTranslationKey();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable("tooltip.meatweapons.meatgun_module_1").formatted(Formatting.GOLD).formatted(Formatting.BOLD));
        if (type.complexity() != 0)
            tooltip.add(Text.translatable("tooltip.meatweapons.meatgun_module.complexity", type.complexity()).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

    // TODO: Why is this necessary? This item should do nothing.
    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return false;
    }
}
