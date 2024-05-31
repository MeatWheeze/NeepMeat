package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.screen_handler.DisplayPlateScreenHandler;
import com.neep.neepmeat.transport.client.screen.VSCScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DisplayPlateScreen extends BaseHandledScreen<DisplayPlateScreenHandler>
{
    private final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/vsc.png");

    public DisplayPlateScreen(DisplayPlateScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 64;
        this.backgroundHeight = 32;

        super.init();

        NMTextField textField = new NMTextField(this.textRenderer, x + 6, y + 7, 3 * 18, 17, Text.of(""))
        {
            @Override
            public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY)
            {
                if (isMouseOver(mouseX, mouseY))
                    DisplayPlateScreen.this.renderTooltip(matrices, NeepMeat.translationKey("screen", "display_plate.capacity"), mouseX, mouseY);
            }
        };

        addDrawableChild(textField);
        textField.drawFancyBackground(false);
//        textField.setTooltip(b -> NeepMeat.translationKey("screen", "display_plate.capacity"));
        textField.setText(String.valueOf(handler.getCapacity()));

        textField.setTextPredicate(s ->
        {
            try
            {
                int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
                return parsed >= 0 && parsed <= 8;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        });

        textField.setChangedListener(s ->
        {
            try
            {
                int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
                handler.setCapacity(parsed);
            }
            catch (NumberFormatException e)
            {
                handler.setCapacity(1);
            }
        });
    }

    @Override
    protected void drawBackground(MatrixStack context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        GUIUtil.drawTexture(TEXTURE, context, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack context, int mouseX, int mouseY)
    {
    }
}
