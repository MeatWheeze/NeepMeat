package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.init.NMComponents;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import dev.onyxstudios.cca.api.v3.item.ItemTagInvalidationListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemWorkpiece extends ItemComponent implements Workpiece, ItemTagInvalidationListener
{
    // TODO: remove
    private static final String KEY = NeepMeat.NAMESPACE + ":work_piece";

    private List<ManufactureStep<?>> stepsCache = null;
    private boolean invalidated;

    public ItemWorkpiece(ItemStack stack)
    {
        super(stack);
    }

    // Checks for validity without implicitly constructing the component.
    public static boolean has(ItemStack stack)
    {
        return stack.getSubNbt(NMComponents.WORKPIECE.getId().toString()) != null;
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
        if (stepsCache != null)
        {
            return stepsCache;
        }

        var nbt = getSubNbt(KEY);
        if (nbt == null)
            return Collections.emptyList();

        var list = getList(nbt);

        List<ManufactureStep<?>> steps = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i)
        {
            var compound = list.getCompound(i);
            String id = compound.getString("id");

            var entry = ManufactureStep.REGISTRY.get(Identifier.tryParse(id));
            if (entry != null)
            {
                steps.add(entry.create(compound.getCompound("sub")));
            }
        }

        stepsCache = steps;
        return steps;
    }

    @Override
    public void clearSteps()
    {
        stepsCache = null;
        var nbt = getSubNbt(KEY);
        if (nbt == null)
            return;

        var list = getList(nbt);
        list.clear();

        // Not sure if the above steps were necessary
        remove(KEY);
    }

    @Override
    public void removeStep(int i)
    {
        stepsCache = null;
        var nbt = getSubNbt(KEY);
        if (nbt == null)
            return;

        var list = getList(nbt);
        list.remove(i);
        nbt.put("steps", list);

        if (list.isEmpty())
        {
            remove(KEY);
        }
        else
        {
            putCompound(KEY, nbt);
        }
    }

    private NbtCompound getSubNbt(String key)
    {
        if (this.getRootTag() == null || !this.getRootTag().contains(key, NbtElement.COMPOUND_TYPE))
        {
            return null;
        }
        return getCompound(key);
    }

    @Override
    public void onTagInvalidated()
    {
        super.onTagInvalidated();
        stepsCache = null;
    }
}
