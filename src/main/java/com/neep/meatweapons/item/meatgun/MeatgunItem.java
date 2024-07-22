package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.item.WeakTwoHanded;
import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public abstract class MeatgunItem extends BaseItem implements Meatgun, WeakTwoHanded, GunItem, OverrideSwingItem
{
    private final Random random = new Random();

    public MeatgunItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(tooltipSupplier, settings.maxCount(1));
    }

    @Override
    public boolean displayArmFirstPerson(ItemStack stack, Hand hand)
    {
        return true;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        MWComponents.MEATGUN.get(stack).trigger(world, player, stack, id, pitch, yaw, handType, hand);
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        MWComponents.MEATGUN.get(stack).release(world, player, stack, id, pitch, yaw, handType, hand);
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        MWComponents.MEATGUN.get(stack).tickTrigger(world, player, stack, id, pitch, yaw, handType, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity player)
        {
            @Nullable MeatgunComponent component = MWComponents.MEATGUN.getNullable(stack);
            if (component != null)
            {
                component.commonTick(player);

                if (world.isClient())
                    component.clientTick(player);
            }
            else
            {
                MeatLib.LOGGER.error("Meatgun component has not been registered for item {}", this);
            }
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, @NotNull PlayerEntity player, StackReference cursorStackReference)
    {
        if (!player.getWorld().isClient())
        {
            AmmunitionProvider provider = AmmunitionProvider.LOOKUP.find(otherStack, cursorStackReference::set);
            if (provider != null)
            {
                MeatgunComponent component = MWComponents.MEATGUN.get(stack);
                component.getRootHolder().reload(provider, player);
                return true;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player)
    {
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        if (tooltipContext.isAdvanced())
            tooltip.add(Text.literal("UUID: " + MWComponents.MEATGUN.get(itemStack).getUuid()).formatted(Formatting.DARK_GRAY));
    }

    @Override
    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        return Vec3d.ZERO;
    }

    @Override
    public void playSound(World world, LivingEntity entity, GunSounds sound)
    {

    }

    @Override
    public void syncAnimation(World world, LivingEntity player, ItemStack stack, String animation, boolean broadcast)
    {

    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public int getShots(ItemStack stack, int trigger)
    {
        return 0;
    }
}
