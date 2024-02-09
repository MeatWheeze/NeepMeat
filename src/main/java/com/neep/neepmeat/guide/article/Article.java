package com.neep.neepmeat.guide.article;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.tablet.ArticleTextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Article
{
    public static final Identifier FONT_ID = new Identifier(NeepMeat.NAMESPACE, "vt323");

    public static Map<String, Function<JsonObject, Content>> DESERIALISERS = new HashMap<>();

    public static final Function<JsonObject, Content> TEXT = DESERIALISERS.put("text",
    object ->
    {
        JsonElement textObject = object.get("text");
        if (textObject.isJsonArray())
        {
            MutableText text = MutableText.of(net.minecraft.text.TextContent.EMPTY);
            textObject.getAsJsonArray().forEach(element ->
            {
                text.append(Text.Serializer.fromJson(element));
            });
            return new TextContent(text);
        }
        MutableText text = Text.Serializer.fromJson(object);
//        text.setStyle(text.getStyle().withFont(FONT_ID));
        return new TextContent(text);
    });

    public static final Function<JsonObject, Content> CODE = DESERIALISERS.put("code",
            object ->
            {
                JsonElement textObject = object.get("text");
                if (textObject.isJsonArray())
                {
                    MutableText text = MutableText.of(net.minecraft.text.TextContent.EMPTY);
                    textObject.getAsJsonArray().forEach(element ->
                    {
                        text.append(Text.Serializer.fromJson(element));
                    });
                    return new CodeContent(text);
                }
                MutableText text = Text.Serializer.fromJson(object);
                return new CodeContent(text);
            });

    public static final Function<JsonObject, Content> CTEXT = DESERIALISERS.put("ctext",
            object ->
            {
                MutableText text = Text.Serializer.fromJson(object);
                return new CenteredTextContent(text);
            });
    public static final Function<JsonObject, Content> IMAGE = DESERIALISERS.put("image",
            object ->
            {
                int width = JsonHelper.getInt(object, "width");
                int height = JsonHelper.getInt(object, "height");
                float scale = 0.5f;
                if (JsonHelper.hasNumber(object, "scale"))
                    scale = JsonHelper.getFloat(object, "scale");
                Identifier image = new Identifier(JsonHelper.getString(object, "path"));
                return new ImageContent(width, height, scale, image);
            });

    public static Article EMPTY = new Article("");

    private final List<Content> contents = new ArrayList<>();
    private final String id;

    private Article(String id)
    {
        this.id = id;
    }

    public static Article fromJson(JsonObject object)
    {
        String id = JsonHelper.getString(object, "id");
        Article article = new Article(id);
        JsonHelper.getArray(object, "contents", new JsonArray()).forEach(
                element ->
                {
                    JsonObject content = (JsonObject) element;

                    // Apply the correct deserialiser and add the result to contents
                    String contentType = JsonHelper.getString(content, "type");
                    article.contents.add(DESERIALISERS.getOrDefault(contentType,
                            o -> {throw new JsonParseException("Unknown article content type '" + contentType + "'");})
                            .apply(content));
                });
        return article;
    }

    public List<Content> getContents()
    {
        return contents;
    }

    public String getId()
    {
        return id;
    }

    // Content takes a fixed width and returns a height
    public interface Content
    {
        int render(MatrixStack matrices, float x, float y, float width, double scroll, ArticleTextWidget parent);
    }
}
