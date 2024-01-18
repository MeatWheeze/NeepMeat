package com.neep.neepmeat.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
{
    @Shadow public ServerPlayerEntity player;

//    @Inject(method = "onPlayerAction", at = @At("HEAD"))
//    void onApplyAction(PlayerActionC2SPacket packet, CallbackInfo ci)
//    {
//        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
//        if (stack.getItem() instanceof AssaultDrillItem drill)
//        {
//            switch(packet.getAction())
//            {
////                case START_DESTROY_BLOCK -> drill.onAttackBlock(player.world, stack, player);
////                case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> drill.onFinishAttackBlock(player.world, stack, player);
//            }
//        }
//    }

//    @Shadow @Final private MinecraftClient client;
//
//    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
//    void onThing(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir)
//    {
//        PlayerEntity player = client.player;
//        World world = client.world;
//        ItemStack active = player.getStackInHand(Hand.MAIN_HAND); // I think block breaking only uses the main hand
//        if (active.getItem() instanceof AssaultDrillItem drill)
//        {
//            drill.onUpdateBreak(world, active, player);
//        }
//    }


//    @Shadow @Final protected ServerPlayerEntity player;
//
//    @Shadow protected ServerWorld world;
//
//    @Shadow private int blockBreakingProgress;
//
//    @Inject(method = "continueMining", at = @At("HEAD"))
//    void onContinueMining(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir)
//    {
//        ItemStack active = player.getStackInHand(Hand.MAIN_HAND); // I think block breaking only uses the main hand
//        if (active.getItem() instanceof AssaultDrillItem drill)
//        {
//            drill.onUpdateBreak(world, active, player, blockBreakingProgress);
//        }
//    }
}
