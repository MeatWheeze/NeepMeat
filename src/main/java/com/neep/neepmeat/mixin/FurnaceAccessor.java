package com.neep.neepmeat.mixin;

import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceAccessor
{
	@Accessor
	int getBurnTime();

	@Accessor("burnTime")
	void setBurnTime(int burnTime);

//	@Invoker("markDirty")
//	static void markDirty(World world, BlockPos pos, BlockState state)
//	{
//		throw new AssertionError();
//	}
}

