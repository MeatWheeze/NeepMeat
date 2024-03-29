package com.neep.neepmeat.implant.item;

import com.google.common.collect.Sets;
import com.neep.neepmeat.implant.player.ImplantManager;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ItemImplantManager extends ItemComponent implements ImplantManager
{
    public ItemImplantManager(ItemStack stack)
    {
        super(stack);
    }

    @Override
    public void installImplant(Identifier implantId)
    {
        NbtList list = getList("implants", NbtElement.COMPOUND_TYPE);

        if (list.contains(implantId))
            return;

        NbtCompound n = new NbtCompound();
        n.putString("id", implantId.toString());
        list.add(n);

        putList("implants", list);
    }

    @Override
    public void removeImplant(Identifier id)
    {
        NbtList list = getList("implants", NbtElement.COMPOUND_TYPE);

        list.removeIf(nbt ->
                ((NbtCompound) nbt).getString("id").equals(id.toString()));

        putList("implants", list);
    }

    @Override
    public Set<Identifier> getInstalled()
    {
        Set<Identifier> ids = Sets.newHashSet();
        NbtList list = getList("implants", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            ids.add(Identifier.tryParse(list.getCompound(i).getString("id")));
        }
        return ids;
    }
}
