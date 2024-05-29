package com.neep.neepmeat.plc.recipe;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.init.NMComponents;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MobWorkpiece implements Workpiece, AutoSyncedComponent
{
    protected final List<ManufactureStep<?>> steps = Lists.newArrayList();
    protected final Entity entity;

    public MobWorkpiece(Entity entity)
    {
        this.entity = entity;
    }

    @Override
    public void addStep(ManufactureStep<?> step)
    {
        steps.add(step);
        NMComponents.WORKPIECE.sync(this.entity);
    }

    @Override
    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }

    @Override
    public void clearSteps()
    {
        steps.clear();
    }

    @Override
    public void removeStep(int i)
    {
        steps.remove(i);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag)
    {
        steps.clear();
        NbtList list = tag.getList("steps", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < list.size(); ++i)
        {
            NbtCompound stepNbt = list.getCompound(i);

            Identifier id = Identifier.tryParse(stepNbt.getString("id"));
            var provider = ManufactureStep.REGISTRY.get(id);
            if (provider != null)
            {
                ManufactureStep<?> step = provider.create(stepNbt.getCompound("step"));
                steps.add(step);
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag)
    {
        NbtList list = new NbtList();
        for (var step : steps)
        {
            // What are you doing, stepNbt?
            NbtCompound stepNbt = new NbtCompound();
            stepNbt.putString("id", step.getId().toString());
            stepNbt.put("step", step.toNbt());
            list.add(stepNbt);
        }
        tag.put("steps", list);
    }
}
