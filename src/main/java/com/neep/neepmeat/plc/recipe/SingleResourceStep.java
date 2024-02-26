//package com.neep.neepmeat.plc.recipe;
//
//import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//
//import java.util.List;
//
//public class Single<T> implements ManufactureStep<T>
//{
//    public SingleResourceStep(NbtCompound nbt)
//    {
//        Identifier id = Identifier.tryParse(nbt.getString("id"));
//        if (id != null)
//        {
//            item = Registries.ITEM.get(id);
//        }
//        else
//            item = Items.AIR;
//    }
//
//    public SingleResourceStep(JsonObject jsonObject)
//    {
//        String idString = JsonHelper.getString(jsonObject, "resource");
//        Identifier id = Identifier.tryParse(idString);
//        this.item = Registries.ITEM.get(id);
//    }
//
//    @Override
//    public void mutate(T t)
//    {
//
//    }
//
//    @Override
//    public Identifier getId()
//    {
//        return null;
//    }
//
//    @Override
//    public void appendText(List<Text> tooltips)
//    {
//
//    }
//
//    @Override
//    public NbtCompound toNbt()
//    {
//        return null;
//    }
//
//    @Override
//    public boolean equalsOther(ManufactureStep<?> other)
//    {
//        return false;
//    }
//}
