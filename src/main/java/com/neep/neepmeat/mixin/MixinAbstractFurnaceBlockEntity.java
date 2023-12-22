package com.neep.neepmeat.mixin;

import com.neep.neepmeat.machine.HeatableFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity implements HeatableFurnace
{
    @Shadow protected abstract boolean isBurning();
    @Shadow int fuelTime;
    @Shadow int burnTime;

    @Shadow protected DefaultedList<ItemStack> inventory;
    @Shadow @Final private RecipeType<? extends AbstractCookingRecipe> recipeType;
    @Shadow private int cookTime;
    protected float heatMultiplier;

    @Override
    public void updateState(World world, BlockPos pos, BlockState oldState)
    {
        if (oldState.get(FurnaceBlock.LIT) != isBurning())
        {
            BlockState newState = oldState.with(FurnaceBlock.LIT, isBurning());
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void setBurning()
    {
        this.burnTime = 2;
    }

    @Override
    public void setHeatMultiplier(float multiplier)
    {
        this.heatMultiplier = multiplier;
    }

    @Override
    public float getHeatMultiplier()
    {
        return heatMultiplier;
    }

    @Override
    public boolean isCooking()
    {
        ItemStack itemStack = inventory.get(1);
        World world = ((BlockEntity) (Object) this).getWorld();
        AbstractFurnaceBlockEntity furnace = ((AbstractFurnaceBlockEntity) (Object) this);
        if (isBurning() || !itemStack.isEmpty() && !inventory.get(0).isEmpty())
        {
            Recipe<?> recipe = world.getRecipeManager().getFirstMatch(recipeType, furnace, world).orElse(null);
            int i = furnace.getMaxCountPerStack();
            return isBurning() && canAcceptRecipeOutput(recipe, inventory, i);
        }
        return false;
    }

    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count)
    {
        if (slots.get(0).isEmpty() || recipe == null)
        {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty())
        {
            return false;
        }
        ItemStack itemStack2 = slots.get(2);
        if (itemStack2.isEmpty())
        {
            return true;
        }
        if (!itemStack2.isItemEqualIgnoreDamage(itemStack))
        {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount())
        {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci)
    {
        // Increment cookTime twice per tick if the heat multiplier rises above a certain value.
        float multiplier = blockEntity.getHeatMultiplier();
        if (blockEntity.isCooking())
        {
            int cookTime = blockEntity.getCookTime();
            int total = blockEntity.getCookTimeTotal();
            int tickIncrement = (int) Math.floor(multiplier * 3);
            // The comparison in AbstractFurnaceBlockEntity uses ==
            blockEntity.setCookTime(Math.min(total - 1, cookTime + tickIncrement));
        }
    }
}