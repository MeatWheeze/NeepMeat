package com.neep.neepmeat.machine.synthesiser;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

public class MobSynthesisRegistry implements SimpleSynchronousResourceReloadListener
{
    private static final MobSynthesisRegistry INSTANCE = new MobSynthesisRegistry();

    private final Map<EntityType<?>, Entry> entries = Maps.newHashMap();
    private final Set<EntityType<?>> denyTypes = Sets.newHashSet();

    @Nullable
    public static Entry get(Entity entity)
    {
        return get(entity.getType());
    }

    @Nullable
    public static Entry get(EntityType<?> type)
    {
        return getInstance().getEntry(type);
    }

    @Nullable
    public Entry getEntry(EntityType<?> type)
    {
        if (denyTypes.contains(type))
            return null;

        return entries.get(type);
    }

    public static long meatFromSize(Entity entity)
    {
        return (long) Math.floor(entity.getHeight() * entity.getWidth() * entity.getWidth() * FluidConstants.BUCKET);
    }

    public static long meatFromSize(EntityType<?> entity)
    {
        return (long) Math.floor(entity.getHeight() * entity.getWidth() * entity.getWidth() * FluidConstants.BUCKET);
    }

    public static MobSynthesisRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void reload(ResourceManager manager)
    {
        entries.clear();
        denyTypes.clear();

        initDefaults();

        for (Identifier id : manager.findResources("cloning", path -> path.getPath().endsWith(".json")).keySet())
        {
            if (manager.getResource(id).isPresent())
            {
                try (InputStream stream = manager.getResource(id).get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);
                    JsonObject rootObject = (JsonObject) rootElement;

                    JsonArray allowJson = JsonHelper.getArray(rootObject, "extra");
                    allowJson.forEach(jsonElement ->
                    {
                        if (jsonElement instanceof JsonObject jsonObject)
                        {
                            if (jsonObject.size() != 1)
                                throw new JsonSyntaxException("Expected entity ID string or object containing entity ID and meat amount");

                            jsonObject.entrySet().forEach(entry ->
                            {
                                String entityIdStr = entry.getKey();
                                Identifier entityId = new Identifier(entityIdStr);
                                int meat = entry.getValue().getAsInt();

                                EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityId);

                                entries.put(entityType, new Entry(entityType, meat, 60));
                            });
                        }
                        else
                        {
                            String entityIdStr = jsonElement.getAsString();
                            Identifier entityId = new Identifier(entityIdStr);
                            EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityId);
                            long meat = meatFromSize(entityType);

                            entries.put(entityType, new Entry(entityType, meat, 60));
                        }
                    });

                    JsonArray denyJson = JsonHelper.getArray(rootObject, "deny");
                    denyJson.forEach(jsonElement ->
                    {
                        JsonHelper.asString(jsonElement, jsonElement.toString());
                        String entityIdStr = jsonElement.getAsString();
                        Identifier entityId = new Identifier(entityIdStr);
                        EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityId);
                        denyTypes.add(entityType);
                    });
                }
                catch (Exception e)
                {
                    NeepMeat.LOGGER.error("Error occurred while loading cloning json " + id.toString(), e);
                }
            }

        }
    }

    private void initDefaults()
    {
        // Generate entries for all spawn eggs based on bounding box size
        SpawnEggItem.getAll().forEach(eggItem ->
        {
            EntityType<?> type = eggItem.getEntityType(null);
            this.entries.put(type, new Entry(type, meatFromSize(type), 60));
        });
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(NeepMeat.NAMESPACE, "cloning");
    }

    public record Entry(EntityType<?> type, long meat, int time)
    {
    }
}
