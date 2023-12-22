package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.NeepMeat;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemWorkpiece extends ItemComponent implements Workpiece
{
    private static final String KEY = NeepMeat.NAMESPACE + ":work_piece";

    public ItemWorkpiece(ItemStack stack)
    {
        super(stack);
    }

    public void addStep(ManufactureStep<?> step)
    {
        Identifier id = step.getId();
        NbtCompound nbt = getCompound(KEY);

        var list = getList(nbt);
        NbtCompound entry = new NbtCompound();
        entry.putString("id", id.toString());
        entry.put("sub", step.toNbt());

        list.add(entry);

        putCompound(KEY, nbt);
    }

    private NbtList getList(NbtCompound nbt)
    {
        // This silently returns a new empty list if it's not present. Very useful (not).
        var list = nbt.getList("steps", NbtElement.COMPOUND_TYPE);
        if (list.isEmpty())
        {
            nbt.put("steps", list);
        }
        return list;
    }

    @Override
    public List<ManufactureStep<?>> getSteps()
    {
        var nbt = getSubNbt(KEY);
        if (nbt == null)
            return Collections.emptyList();

        var list = getList(nbt);

        // TODO: cache
        List<ManufactureStep<?>> steps = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i)
        {
            var compound = list.getCompound(i);
            String id = compound.getString("id");

            var entry = ManufactureStep.REGISTRY.get(id);
            if (entry != null)
            {
                steps.add(entry.create(compound.getCompound("sub")));
            }
        }

        return steps;
    }

    private NbtCompound getSubNbt(String key)
    {
        if (this.getRootTag() == null || !this.getRootTag().contains(key, NbtElement.COMPOUND_TYPE))
        {
            return null;
        }
        return getCompound(key);

    }
}
