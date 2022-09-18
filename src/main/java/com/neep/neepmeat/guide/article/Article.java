package com.neep.neepmeat.guide.article;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neep.neepmeat.client.screen.tablet.ArticleTextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Article
{
    public static Map<String, Function<JsonObject, Content>> DESERIALISERS = new HashMap<>();

    public static final Function<JsonObject, Content> TEXT = DESERIALISERS.put("text",
    object ->
    {
        MutableText text = Text.Serializer.fromJson(object);
        return new TextContent(text);
    });

    public static Article EMPTY = new Article();

    private final List<Content> contents = new ArrayList<>();

    private Article()
    {

    }

    public static Article fromJson(JsonObject object)
    {
        Article article = new Article();
        JsonHelper.getArray(object, "contents", new JsonArray()).forEach(
                element ->
                {
                    JsonObject content = (JsonObject) element;

                    // Apply the correct deserialiser and add the result to contents
                    String contentType = JsonHelper.getString(content, "type");
                    article.contents.add(DESERIALISERS.get(contentType).apply(content));
                });
        return article;
    }

    public List<Content> getContents()
    {
        return contents;
    }

    // Content takes a fixed width and returns a height
    public interface Content
    {
        int render(MatrixStack matrices, float x, float y, float width, double scroll, ArticleTextWidget parent);
    }
}
