package com.neep.neepmeat.client.screen.tablet;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.guide.GuideNode;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.screen_handler.TerminalScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Environment(value= EnvType.CLIENT)
public class TabletMenuScreen extends TabletScreen
{
    public static final Identifier TERMINAL_ICON = new Identifier(NeepMeat.NAMESPACE, "textures/gui/tablet/widgets/terminal.png");

    // Current location within the entry tree
    protected final Deque<GuideNode> path = new LinkedList<>();

    // Currently available entries
    protected final List<EntryWidget> entries = new ArrayList<>();

    private int menuPage;

    public TabletMenuScreen(PlayerEntity player)
    {
        super(player, player.currentScreenHandler);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        GuideNode root = GuideReloadListener.getInstance().getRootNode();
        if (root == null)
        {
            throw new IllegalStateException("Guide tablet tree is not loaded. Report this to mod author.");
        }
        path.push(root);
    }

    public static TabletScreenFactory getFactory(PlayerEntity player)
    {
        return new TabletScreenFactory(TERMINAL_ICON, () -> new TabletMenuScreen(player), TerminalScreenHandler::new);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void handledScreenTick()
    {
        super.handledScreenTick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode != GLFW.GLFW_KEY_ESCAPE)
        {
            return false;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (!(chr == GLFW.GLFW_KEY_ESCAPE))
        {
        }
        super.charTyped(chr, modifiers);
        return true;
    }

    @Override
    public void init()
    {
        super.init();
        generateMenu();
    }

    protected void generateMenu()
    {
        entries.forEach(this::remove);
        entries.clear();

        int entryHeight = 16;
        List<GuideNode> nodes = path.peek().getChildren();
        // TODO: pages
        for (int i = 0; i < nodes.size(); ++i)
        {
            GuideNode node = nodes.get(i);
            ItemStack icon = new ItemStack(Registry.ITEM.get(node.getIcon()));
            entries.add(new EntryWidget(this.x, this.y + i * entryHeight, 32, entryHeight, icon, node.getText(), node));
        }
        entries.forEach(this::addDrawableChild);
    }

    protected int getPageEntries()
    {
        return 10;
    }

    public class EntryWidget extends ClickableWidget
    {
        private final GuideNode node;

        public EntryWidget(int x, int y, int w, int h, ItemStack icon, Text text, GuideNode node)
        {
            super(x, y, w, h, text);
            this.node = node;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.onPress();
        }

        protected void onPress()
        {
            path.push(node);
            init();
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            super.renderButton(matrices, mouseX, mouseY, delta);
        }
    }
}