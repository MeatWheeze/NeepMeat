package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBehaviour;
import com.neep.neepmeat.screen_handler.ContentDetectorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(BACKGROUND, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public void buttonPress(int id)
    {
        this.client.interactionManager.clickButton(this.handler.syncId, id);
    }

    @Override
    protected void init()
    {
        super.init();

        this.behaviourButton = new CyclingButtonWidget(this.x + 20, this.y + 16, 32,
                16, 0, 8, 32, 3, COUNT_WIDGET, 32, 128, Text.of("uwu"), (button, mouseButton) ->
        {
            this.buttonPress(InventoryDetectorBehaviour.DEL_COUNT);
        });
//        (buttonWidget, matrices, mouseX, mouseY) ->
//        {
//            drawMouseoverTooltip(null, Text.of("Stack Condition"), mouseX, mouseY);
//        });

        this.countButton = new CyclingButtonWidget(this.x + 20, this.y + 54, 32, 16, 0, 8,
                32, 1, BEHAVIOUR_WIDGET, 32, 64, Text.of("uwu"), (button, mouseButton) ->
        {
            this.buttonPress(InventoryDetectorBehaviour.DEL_BEHAVIOUR);
        });
//        (buttonWidget, matrices, mouseX, mouseY) ->
//        {
//            renderTooltip(matrices, Text.of("owo"), mouseX, mouseY);
//            switch (((CyclingButtonWidget) buttonWidget).index)
//            {
//                case 0:
//                    renderTooltip(matrices, List.of(Text.of("Regulate"), Text.of("Stays powered until all filter items have left")), mouseX, mouseY);
//                    break;
//                case 1:
//                    renderTooltip(matrices, List.of(Text.of("Absolute"), Text.of("Stays powered only while conditions are met")), mouseX, mouseY);

//            }
//        });


        this.addDrawableChild(countButton);
        this.addDrawableChild(behaviourButton);

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void handledScreenTick()
    {
        behaviourButton.setIndex(handler.delegate.get(InventoryDetectorBehaviour.DEL_COUNT));
        countButton.setIndex(handler.delegate.get(InventoryDetectorBehaviour.DEL_BEHAVIOUR));
    }
}
