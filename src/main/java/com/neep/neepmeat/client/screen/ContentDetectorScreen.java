package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.content_detector.ContentDetectorBehaviour;
import com.neep.neepmeat.screen_handler.ContentDetectorScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ContentDetectorScreen extends HandledScreen<ContentDetectorScreenHandler>
{
    private static final Identifier BACKGROUND = new Identifier("minecraft", "textures/gui/container/dispenser.png");
    private static final Identifier TEST_WIDGET = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget_test.png");
    private static final Identifier COUNT_WIDGET = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/count.png");
    private static final Identifier BEHAVIOUR_WIDGET = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/detector_behaviour.png");

    public CyclingButtonWidget countButton;
    public CyclingButtonWidget behaviourButton;

    public ContentDetectorScreen(ContentDetectorScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    public void buttonPress(int id)
    {
        this.client.interactionManager.clickButton(this.handler.syncId, id);
    }

    public void updateButtons()
    {

    }

    @Override
    protected void init()
    {
        super.init();

        this.behaviourButton = new CyclingButtonWidget(this.x + 20, this.y + 15, 32,
                32, 0, 0, 32, 3, COUNT_WIDGET, 32, 128, Text.of("uwu"), button ->
        {
            this.buttonPress(ContentDetectorBehaviour.DEL_COUNT);
//            ((CyclingButtonWidget) button).setIndex(handler.delegate.get(ContentDetectorBehaviour.DEL_COUNT));
        });

        this.countButton = new CyclingButtonWidget(this.x + 20, this.y + 40, 32, 32, 0, 0,
                32, 1, BEHAVIOUR_WIDGET, 32, 64, Text.of("uwu"), button ->
        {
            this.buttonPress(ContentDetectorBehaviour.DEL_BEHAVIOUR);
//            ((CyclingButtonWidget) button).setIndex(handler.delegate.get(ContentDetectorBehaviour.DEL_BEHAVIOUR));
        });


        this.addDrawableChild(countButton);
        this.addDrawableChild(behaviourButton);

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void handledScreenTick()
    {
        behaviourButton.setIndex(handler.delegate.get(ContentDetectorBehaviour.DEL_COUNT));
        countButton.setIndex(handler.delegate.get(ContentDetectorBehaviour.DEL_BEHAVIOUR));
    }
}
