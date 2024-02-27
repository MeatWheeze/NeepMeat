package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CheeseCleaverItem extends BaseSwordItem
{
//    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static String CONTROLLER_NAME = "controller";

    public CheeseCleaverItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.DIAMOND, 4, -3f, settings);
    }

    public static void writeCharged(ItemStack stack, boolean charged)
    {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("charged", charged);
        stack.writeNbt(nbt);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        super.postHit(stack, target, attacker);

        if (stack.getOrCreateNbt().getBoolean("charged"))
        {
            target.addVelocity(0, 0.4, 0);
            writeCharged(stack, false);
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        user.setCurrentHand(hand);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        if (stack.getOrCreateNbt().getBoolean("charged"))
        {
            return UseAction.NONE;
        }
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
       writeCharged(stack, true);
       return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 20;
    }
}
