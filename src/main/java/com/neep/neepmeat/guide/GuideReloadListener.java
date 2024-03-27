package com.neep.neepmeat.guide;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.guide.article.Article;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class GuideReloadListener implements SimpleSynchronousResourceReloadListener
{
    private static final GuideReloadListener INSTANCE = new GuideReloadListener();

    private GuideNode root;
    private final Map<String, Article> articles = new HashMap<>();
    private final Collection<GuideNode.ArticleNode> articleNodes = new ArrayList<>();

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
        articleNodes.clear();
        articles.clear();

        var map = manager.findResources("guide", path -> path.getPath().endsWith(".json")).keySet();
        for(Identifier id : manager.findResources("guide", path -> path.getPath().endsWith(".json")).keySet())
        {
            var opt = manager.getResource(id);
            if (opt.isPresent())
            {
                try(InputStream stream = opt.get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);

                    processArticles((JsonObject) rootElement);
                    root = processNode(JsonHelper.getObject((JsonObject) rootElement, "tree"));
                }
                catch(Exception e)
                {
                    NeepMeat.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
                }
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

            // Using recursion because I can't be bothered to traverse it properly
            JsonHelper.getArray(object, "entries").forEach(o -> nodes.add(processNode((JsonObject) o)));

            return new GuideNode.MenuNode(id, new Identifier(icon), Text.of(text), nodes);
        }

        Set<String> lookup = new HashSet<>();
        if (JsonHelper.hasArray(object, "lookup"))
        {
            for (var term : JsonHelper.getArray(object, "lookup"))
            {
                lookup.add(term.getAsString());
            }
        }
        else if (JsonHelper.hasString(object, "lookup"))
        {
            lookup.add(JsonHelper.getString(object, "lookup"));
        }

        GuideNode.ArticleNode node = new GuideNode.ArticleNode(id, lookup, new Identifier(icon), Text.of(text));
        articleNodes.add(node);
        return node;
    }

    public GuideNode getRootNode()
    {
        return root;
    }

    public Article getArticle(String id)
    {
        return articles.get(id);
    }

    @Nullable
    public GuideNode.ArticleNode getArticleByItem(ItemStack stack)
    {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        return articleNodes.stream().filter(n -> n.getId().equals(itemId.getPath())).findFirst().orElse(null);
    }

    @Nullable
    public List<GuideNode> getPath(ItemStack stack)
    {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        GuideNode target = articleNodes.stream().filter(n -> n.matchesLookupTerm(itemId.toString())).findFirst().orElse(null);
        if (target != null)
        {
            List<GuideNode> route = new ArrayList<>();
            recursiveDfs(root, target, route);

            // If no route is found, we will go directly to the target.
            route.add(target);

            return route;
        }
        return null;
    }

    private boolean recursiveDfs(GuideNode node, GuideNode target, List<GuideNode> route)
    {
        route.add(route.size(), node);
        for (var child : node.getChildren())
        {
            if (child == target)
                return true;

            boolean foundSub = recursiveDfs(child, target, route);
            if (foundSub)
                return true;
        }
        route.remove(route.size() - 1);
        return false;
    }

    public Collection<Article> getArticles()
    {
        return articles.values();
    }

    public Collection<GuideNode.ArticleNode> getArticleNodes()
    {
        return articleNodes;
    }
}
