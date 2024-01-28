package com.neep.neepmeat.enlightenment;

import com.neep.neepmeat.api.enlightenment.EnlightenmentEvent;
import com.neep.neepmeat.init.NMEntities;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LimbEnlightenmentEvent implements EnlightenmentEvent
{
    private final World world;
    private final ServerPlayerEntity serverPlayer;
    private final Random random;

    public LimbEnlightenmentEvent(World world, ServerPlayerEntity serverPlayer)
    {
        this.world = world;
        this.random = Random.create();
        this.serverPlayer = serverPlayer;
    }

    @Override
    public void tick()
    {
        BlockPos playerPos = serverPlayer.getBlockPos();
        int attempt = 0;
        while (attempt < 10)
        {
            attempt++;

            double d = playerPos.getX() + (this.random.nextDouble() - 0.5) * 64.0;
            double e = playerPos.getY() + (double)(this.random.nextInt(64) - 32);
            double f = playerPos.getZ() + (this.random.nextDouble() - 0.5) * 64.0;

            BlockPos.Mutable mutable = new BlockPos.Mutable(d, e, f);
            while (mutable.getY() > this.world.getBottomY() && this.world.getBlockState(mutable).getMaterial().blocksMovement())
            {
                mutable.move(Direction.DOWN);
            }

            if (world.isOutOfHeightLimit(mutable.getY()) || !mutable.isWithinDistance(playerPos, 20) || !world.isSpaceEmpty(new Box(mutable)))
            {
                continue;
            }

            int spawnTries = random.nextInt(5);
            for (int i = 0; i < spawnTries; ++i)
            {
                var limb = NMEntities.LIMB.create(world);
                world.spawnEntity(limb);
                limb.setPos(mutable.getX() + random.nextDouble(), mutable.getY() + 0.1, mutable.getZ() + random.nextDouble());
            }

            break;
        }
    }

    @Override
    public boolean isRemoved()
    {
        return true;
    }
}
