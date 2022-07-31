package com.neep.neepmeat.mixin;

import com.neep.neepmeat.machine.IHeatable;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceAccessor extends IHeatable
{
	@Accessor
	int getBurnTime();

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
}

