package com.neep.meatweapons.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.implant.MagazineOrganImplant;
import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.neepmeat.implant.player.PlayerImplantManager;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AmmunitionItem extends BaseItem
{
    public AmmunitionItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(tooltipSupplier, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.EAT;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        PlayerImplantManager manager = (PlayerImplantManager) NMComponents.IMPLANT_MANAGER.get(user);
        ItemStack itemStack = user.getStackInHand(hand);

        @Nullable MagazineOrganImplant implant = manager.getImplant(MagazineOrganImplant.ID);

        if (implant != null)
        {
            if (implant.canConsume(this, itemStack))
            {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            }
            else
            {
                return TypedActionResult.fail(itemStack);
            }
        }
        else
        {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 32;
    }

    @Override
    public SoundEvent getEatSound()
    {
        return NMSounds.ROCK_DRILL;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (user instanceof PlayerEntity player
                && NMComponents.IMPLANT_MANAGER.getNullable(user) instanceof PlayerImplantManager manager)
        {
            PlayerInventory inventory = player.getInventory();
            AmmunitionProvider.Context context = new AmmunitionProvider.InventoryContext(player.getInventory(), inventory.selectedSlot);
            @Nullable AmmunitionProvider provider = AmmunitionProvider.LOOKUP.find(stack, context);
            @Nullable MagazineOrganImplant implant = manager.getImplant(MagazineOrganImplant.ID);

            if (provider != null && implant != null)
            {
                return implant.consume(world, stack, provider);
            }
        }

        return super.finishUsing(stack, world, user);
    }
}
