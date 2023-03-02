package com.neep.neepmeat.mixin;

import com.neep.neepmeat.machine.IHeatable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceAccessor
{
	@Accessor
	int getBurnTime();

	@Accessor
	int getFuelTime();

	@Accessor
	int getCookTimeTotal();

	@Accessor
	int getCookTime();

	@Accessor
	RecipeType<? extends AbstractCookingRecipe> getRecipeType();

	@Accessor("inventory")
	DefaultedList<ItemStack> getInventory();

	@Accessor("burnTime")
	void setBurnTime(int burnTime);

	@Accessor("fuelTime")
	void setFuelTime(int fuelTime);

	@Accessor("cookTime")
	void setCookTime(int cookTime);

	@Invoker("getFuelTime")
	int callGetFuelTime(ItemStack fuel);

	@Invoker("isBurning")
	boolean callIsBurning();

}

