package com.neep.neepmeat.guide;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.guide.article.Article;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuideReloadListener implements SimpleSynchronousResourceReloadListener
{
    private static final GuideReloadListener INSTANCE = new GuideReloadListener();

    private GuideNode root;
    private final Map<String, Article> articles = new HashMap<>();

    public static GuideReloadListener getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(NeepMeat.NAMESPACE, "guide");
    }

    @Override
    public void reload(ResourceManager manager)
    {
        root = null;

        for(Identifier id : manager.findResources("guide", path -> path.endsWith(".json")))
        {
            try(InputStream stream = manager.getResource(id).getInputStream())
            {
                Reader reader = new InputStreamReader(stream);
                JsonElement rootElement = JsonParser.parseReader(reader);

                processArticles((JsonObject) rootElement);
                root = processNode(JsonHelper.getObject((JsonObject) rootElement, "tree"));


                System.out.println(root.getChildren().get(0));
            }
            catch(Exception e)
            {
                NeepMeat.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        }
    }

    protected void processArticles(JsonObject root)
    {
        JsonArray array = JsonHelper.getArray(root, "articles", new JsonArray());
        array.forEach(e ->
        {
            String id = JsonHelper.getString((JsonObject) e, "id");
            Article article = Article.fromJson((JsonObject) e);
            articles.put(id, article);
        });
    }

    protected GuideNode processNode(JsonObject object)
    {
        String id = JsonHelper.getString(object, "id");
        String icon = JsonHelper.getString(object, "icon");
        String text = JsonHelper.getString(object, "text");
        if (JsonHelper.hasArray(object, "entries"))
        {
            List<GuideNode> nodes = new ArrayList<>();
            JsonHelper.getArray(object, "entries").forEach(o -> nodes.add(processNode((JsonObject) o)));

            // Using recursion because I can't be bothered to traverse it properly
            return new GuideNode.MenuNode(id, new Identifier(icon), Text.of(text), nodes);
        }
        return new GuideNode.ArticleNode(id, new Identifier(icon), Text.of(text));
    }

    public GuideNode getRootNode()
    {
        return root;
    }

    public Article getArticle(String id)
    {
        return articles.get(id);
    }
}
