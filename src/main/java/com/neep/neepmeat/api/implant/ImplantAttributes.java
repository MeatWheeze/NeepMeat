package com.neep.neepmeat.api.implant;

import com.google.common.collect.Maps;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ImplantAttributes
{
    private static final Map<Identifier, Entry> NAMES = Maps.newHashMap();

    public static void register(Identifier id, Entry entry)
    {
        NAMES.put(id, entry);
    }

    public static Entry get(Identifier id)
    {
        return NAMES.get(id);
    }

    public static Text getName(Identifier id)
    {
//        var entry = NAMES.get(id);
//        if (entry == null)
//        {
//            return Text.of(id.toString());
//        }
        return Text.translatable(id.toTranslationKey("implant"));
    }

    public record Entry(Text name)
    {

    }
}
