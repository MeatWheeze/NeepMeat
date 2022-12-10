package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.surgery.SurgeryRecipe;
import com.neep.neepmeat.recipe.surgery.TableComponent;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TableControllerBlockEntity extends BloodMachineBlockEntity
{
    private SurgeryTableContext context = new SurgeryTableContext();

    public TableControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TableControllerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TABLE_CONTROLLER, pos, state);
    }

    public void assemble()
    {
        context.clear();

        Direction facing = getCachedState().get(BaseHorFacingBlock.FACING).getOpposite();
        Direction left = facing.rotateYCounterclockwise();
        BlockPos corner = pos.offset(facing).offset(left).up();

        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int j = 2; j >= 0; --j)
        {
            for (int i = 0; i < 3; ++i)
            {
                Vec3i xVec = left.getOpposite().getVector().multiply(i);
                Vec3i zVec = facing.getVector().multiply(j);
                mutable.set(corner, xVec);
                mutable.set(mutable, zVec);
                context.add((ServerWorld) world, mutable);
//                if (caches.get(caches.size() - 1).find(null) != null)
//                    ((ServerWorld) world).spawnParticles(ParticleTypes.COMPOSTER, mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5, 5, 0, 0, 0, 0);
            }
        }
    }

    public void testRecipe()
    {
        SurgeryRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.SURGERY, context).orElse(null);
        NeepMeat.LOGGER.info("Recipe: " + recipe);
        if (recipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                recipe.takeInputs(context, transaction);
                transaction.abort();
            }
        }
    }
}
