package com.neep.neepmeat.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceMixin
{
	@Accessor
	int getBurnTime();

	@Accessor("burnTime")
	void setBurnTime(int burnTime);

//	@Inject(at = @At("HEAD"), method = "init()V")
//	private void init(CallbackInfo info)
//    {
//		ExampleMod.LOGGER.info("This line is printed by an example mod mixin!");
//	}
}
