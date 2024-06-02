package com.neep.neepmeat.machine.fabricator;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FabricatorScreen extends BaseHandledScreen<FabricatorScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/fabricator.png");

    private ItemStack result = ItemStack.EMPTY;

    public FabricatorScreen(FabricatorScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        ClientPlayNetworking.registerReceiver(FabricatorScreenHandler.CHANNEL_ID, this::receive);
    }

    void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        boolean present = buf.readBoolean();
        Identifier id = buf.readIdentifier();
        client.execute(() ->
        {
            updateRecipe(present, id);
        });
    }

    void updateRecipe(boolean present, Identifier id)
    {
        if (!present)
            result = ItemStack.EMPTY;

        if (client == null || client.world == null)
            return;

        if (client.world.getRecipeManager().get(id).orElse(null) instanceof CraftingRecipe recipe)
            result = recipe.getOutput();
        else
            result = ItemStack.EMPTY;
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 176;
        this.backgroundHeight = 90 + 1 + 90;
        super.init();

        var resultWidget = addDrawable(new ResultWidget(x + 113, y + 38, MinecraftClient.getInstance().getItemRenderer()));
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta)
    {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        GUIUtil.drawTexture(TEXTURE, matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        GUIUtil.drawInventoryBackground(matrices, i, j + 91);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        GUIUtil.drawText(matrices, textRenderer, this.title, this.playerInventoryTitleX, this.titleY, 0x404040, false);
    }

    @Override
    public void close()
    {
        super.close();
        ClientPlayNetworking.unregisterReceiver(FabricatorScreenHandler.CHANNEL_ID);
    }

    private class ResultWidget implements Drawable
    {
        private final int x;
        private final int y;
        private final ItemRenderer itemRenderer;

        private ResultWidget(int x, int y, ItemRenderer itemRenderer)
        {
            this.x = x;
            this.y = y;
            this.itemRenderer = itemRenderer;
        }

        @Override
        public void render(MatrixStack context, int mouseX, int mouseY, float delta)
        {
            itemRenderer.renderInGuiWithOverrides(client.player, result, x, y, 0);
            itemRenderer.renderGuiItemOverlay(textRenderer, result, x, y);

            if (!result.isEmpty() && mouseX > x && mouseX < x + 18 && mouseY > y && mouseY < y + 18)
            {
                renderTooltip(context, getTooltipFromItem(result), mouseX, mouseY);
            }
        }
    }
}
