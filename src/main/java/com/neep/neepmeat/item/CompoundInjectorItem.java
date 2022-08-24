package com.neep.neepmeat.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CompoundInjectorItem extends Item implements IMeatItem
{
    protected final String registryName;
    public final int healsFor = 8; // Health replenished each use (discounting initial damage)
    public final int initialDamage = 2;

    public CompoundInjectorItem(String name, FabricItemSettings settings)
    {
          super(settings.maxDamage(2).maxDamageIfAbsent(2));
          this.registryName = name;
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public int getItemBarColor(ItemStack stack)
    {
        return MathHelper.hsvToRgb(0f, 1.0F, 1.0F);
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return hasUses(stack) ? UseAction.BLOCK : UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 20;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (!world.isClient && user instanceof PlayerEntity)
        {
            use(stack, (PlayerEntity) user);
        }
        if (world.isClient && user instanceof ClientPlayerEntity)
        {
//            ClientPlayerEntity clientPlayer = (ClientPlayerEntity) user;
//            MinecraftClient.getInstance().player.
        }
        return user.eatFood(world, stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack stack = user.getStackInHand(hand);
        if (hasUses(stack))
        {
            user.setCurrentHand(hand);
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        ItemStack cursorStack = cursorStackReference.get();
        if (cursorStack.getItem().equals(NMItems.CRUDE_INTEGRATION_CHARGE) && stack.getDamage() != 0)
        {
            stack.setDamage(0);
            cursorStack.decrement(1);
            return true;
        }
        return false;
    }
    public boolean hasUses(ItemStack stack)
    {
        return stack.getDamage() < this.getMaxDamage();
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        int heals = getHealsRemaining(stack);
        if (heals > 0 && entity instanceof PlayerEntity)
        {
            ((PlayerEntity) entity).heal(1);
            setHealsRemaining(stack, heals - 1);
        }
    }

    public static void setHealsRemaining(ItemStack stack, int heals)
    {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("Heals", heals);
    }

    public static int getHealsRemaining(ItemStack stack)
    {
        NbtCompound nbt = stack.getNbt();
        return nbt != null ? nbt.getInt("Heals") : 0;
    }

    public int getHealsFor()
    {
        return healsFor;
    }

    private void use(ItemStack stack, PlayerEntity player)
    {
        if (hasUses(stack))
        {
            player.getEntityWorld().playSound(null, player.getBlockPos(), NMSounds.COMPOUND_INJECTOR, SoundCategory.PLAYERS, 1f, 1f);
            if (!player.isCreative())
            {
                stack.setDamage(stack.getDamage() + 1);
            }
            // Damage player by one heart.
            player.damage(DamageSource.MAGIC, initialDamage);
            setHealsRemaining(stack, healsFor + initialDamage);
        }
    }
}
