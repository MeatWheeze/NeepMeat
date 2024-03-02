package com.neep.neepmeat.client.screen.plc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.plc.PLCSyncThings;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Collectors;

import static com.neep.neepmeat.client.screen.tablet.GUIUtil.drawHorizontalLine1;
import static com.neep.neepmeat.client.screen.tablet.GUIUtil.drawVerticalLine1;

public class PLCProgramScreen extends Screen implements ScreenHandlerProvider<PLCScreenHandler>
{
    public static final Identifier WIDGETS = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/plc_widgets.png");
    protected static final Identifier VIGNETTE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/plc_robot_vignette.png");
    protected final PLCScreenEditorState editor;
    protected final PLCScreenShellState shell;
    private final PLCScreenHandler handler;
    private final PLCBlockEntity plc;
    // Text relating to the block that the mouse is currently over
    private final List<Text> tooltipText = Lists.newArrayList();
    protected PLCScreenState state;
    private double mouseX;
    private double mouseY;
    private RecordMode mode;

    public PLCProgramScreen(PLCScreenHandler handler, PlayerInventory playerInventory, Text unused)
    {
        super(unused);
        this.handler = handler;
        this.plc = handler.getPlc();

        this.editor = new PLCScreenEditorState(this);
        this.shell = new PLCScreenShellState(this);
        this.state = shell;
        this.mode = handler.getMode();
    }

    @Nullable
    private static BlockState getWallState(World world, Camera camera)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        float width = 0.8f;
        for (int i = 0; i < 8; ++i)
        {
            Vec3d camPos = camera.getPos();
            double d = camPos.x + (((i >> 0) % 2) - 0.5f) * width * 0.8f;
            double e = camPos.y + (((i >> 1) % 2) - 0.5f) * 0.1f;
            double f = camPos.z + (((i >> 2) % 2) - 0.5f) * width * 0.8f;
            mutable.set(d, e, f);

            BlockState blockState = world.getBlockState(mutable);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || !blockState.shouldBlockVision(world, mutable))
                continue;
            return blockState;
        }
        return null;
    }

    private static void makeEntityTooltip(@Nullable Entity entity, List<Text> tooltip)
    {
        if (entity == null)
            return;

        tooltip.add(entity.getDisplayName());
        Workpiece workpiece = NMComponents.WORKPIECE.getNullable(entity);
        if (workpiece != null)
        {
            for (var step : workpiece.getSteps())
            {
                step.appendText(tooltip);
            }
        }
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    protected void init()
    {
        super.init();

        editor.init(width, height);
        shell.init(width, height);
        if (mode == RecordMode.EDIT)
        {
            addDrawableChild(editor);
            editor.setDimensions(width, height);

            addDrawableChild(new ModeSwitchButton(width - 17, 1, 16, 16));
            addDrawableChild(new StopButton(width - 2 * 17, 1, 16, 16, Text.of("Stop")));
            addDrawableChild(new RunButton(width - 3 * 17, 1, 16, 16, Text.of("Run")));
            addDrawableChild(new CompileButton(width - 4 * 17, 1, 16, 16, Text.of("Compile")));
            state = editor;
        }
        else
        {
            addDrawableChild(shell);
            addDrawableChild(new ModeSwitchButton(width - 17, 1, 16, 16));
            addDrawableChild(new StopButton(width - 2 * 17, 1, 16, 16, Text.of("Stop")));
            state = shell;
        }

    }

    @Override
    public void tick()
    {
        super.tick();
        tickTooltip(mouseX, mouseY);

        editor.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (!state.isSelected() || keyCode == GLFW.GLFW_KEY_ESCAPE)
            return super.keyPressed(keyCode, scanCode, modifiers);

        state.onKeyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        BlockState wallState = getWallState(client.world, client.gameRenderer.getCamera());
        if (wallState != null)
        {
            // Fill screen with the block's particle sprite to prevent xray vision
            Sprite sprite = client.getBlockRenderManager().getModels().getModelParticleSprite(wallState);
            renderInWallOverlay(sprite, matrices.getMatrices());
        }
        else
        {
            // Red fleshy vignette
            drawScreenTexture(matrices.getMatrices(), VIGNETTE, 0, 0, 1, 1, 0.9f);
        }

        if (!tooltipText.isEmpty())
        {
            renderTooltipText(matrices, tooltipText, true, mouseX, mouseY, 0);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawScreenTexture(MatrixStack matrices, Identifier texture, float u0, float v0, float u1, float v1, float light)
    {
        float x0 = 0;
        float y0 = 0;
        float x1 = width;
        float y1 = height;
        float z = 0;
        var matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).color(light, light, light, 1).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, z).color(light, light, light, 1).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, z).color(light, light, light, 1).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, z).color(light, light, light, 1).texture(u0, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    private void renderInWallOverlay(Sprite sprite, MatrixStack matrices)
    {
        float x0 = 0;
        float y0 = 0;
        float x1 = width;
        float y1 = height;
        float z = 0;
        RenderSystem.setShaderTexture(0, sprite.getAtlasId());
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float l = sprite.getMinU();
        float m = sprite.getMaxU();
        float n = sprite.getMinV();
        float o = sprite.getMaxV();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x0, y1, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, o).next();
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, o).next();
        bufferBuilder.vertex(matrix4f, x1, y0, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, n).next();
        bufferBuilder.vertex(matrix4f, x0, y0, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, n).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    private void tickTooltip(double mouseX, double mouseY)
    {
        tooltipText.clear();
        var result = raycastClick(mouseX, mouseY, 15);
        if (result.getType() == HitResult.Type.BLOCK)
        {
            PLCHudRenderer.HIT_RESULT = result;

            var mip1 = MutateInPlace.ITEM.find(client.world, result.getBlockPos(), null);
            var mip2 = MutateInPlace.ENTITY.find(client.world, result.getBlockPos(), null);
            if (mip1 != null)
            {
                var stack = mip1.get();
                if (stack == null || stack.isEmpty())
                    return;

                tooltipText.addAll(mip1.get().getTooltip(client.player, TooltipContext.Default.BASIC));
            }
            else if (mip2 != null)
            {
                makeEntityTooltip(mip2.get(), tooltipText);
            }
        }
        else
        {
            PLCHudRenderer.HIT_RESULT = null;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_3)
        {
            PLCHudRenderer renderer = PLCHudRenderer.getInstance();
            if (renderer != null)
            {
                double sensitivity = 0.4;
                PLCMotionController controller = renderer.getController();
                controller.setPitchYaw(
                        (float) MathHelper.clamp(controller.getPitch() - (deltaY * sensitivity), -90, 90),
                        (float) (controller.getYaw() - deltaX * sensitivity));
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }

        setFocused(null);
        return handleWorldClick(mouseX, mouseY, button);
    }

    protected boolean handleWorldClick(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_3)
            return false;

        var result = raycastClick(mouseX, mouseY, 15);

        if (result.getType() == HitResult.Type.MISS)
        {
            return false;
        }

        addArgument(result);

        for (int i = 0; i < 15; ++i)
        {
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0f));
        }

        return true;
    }

    protected void addArgument(BlockHitResult result)
    {
        state.argument(new Argument(result.getBlockPos(), result.getSide()));
    }

    protected BlockHitResult raycastClick(double mouseX, double mouseY, double range)
    {
        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        Vec3d farPos = screenToWorld(mouseX, mouseY, 0.4f);
        Vec3d nearPos = screenToWorld(mouseX, mouseY, 0.0f);

        Vec3d newFar = nearPos.add(farPos.subtract(nearPos).normalize().multiply(range));

        RaycastContext raycastContext = new RaycastContext(
                nearPos.add(camPos),
                newFar.add(camPos),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, client.player);

        return client.world.raycast(raycastContext);
    }

    public Vec3d screenToWorld(double mouseX, double mouseY, double z)
    {
        var window = client.getWindow();

        Vector3d worldPos = new Matrix4d(PLCHudRenderer.PROJECTION)
                .mul(PLCHudRenderer.MODEL_VIEW)
                .unproject(mouseX, (window.getScaledHeight() - mouseY), z,
                        new int[]{0, 0, window.getScaledWidth(), window.getScaledHeight()}, new Vector3d());

        return new Vec3d(worldPos.x, worldPos.y, worldPos.z);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed()
    {
        super.removed();
        if (client.player != null)
            handler.onClosed(client.player);

        PLCHudRenderer.leave();
    }

    @Override
    public void close()
    {
        super.close();
        this.client.player.closeHandledScreen();
        PLCHudRenderer.leave();
    }

    public void renderTooltipText(DrawContext matrices, List<Text> texts, boolean offset, int x, int y, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(t -> TooltipComponent.of(t.asOrderedText())).collect(Collectors.toList()), offset, x, y, 0, col);
    }

    public void renderTooltipOrderedText(DrawContext matrices, List<OrderedText> texts, boolean offset, int x, int y, int width, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(TooltipComponent::of).collect(Collectors.toList()), offset, x, y, width, col);
    }

    private void renderTooltipComponents(DrawContext context, List<TooltipComponent> components, boolean offset, int x, int y, int maxWidth, int col)
    {
        MatrixStack matrices = context.getMatrices();
        if (offset)
        {
            x += 12;
            y -= 12;
        }
        if (components.isEmpty())
        {
            return;
        }

        int maxHeight = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components)
        {
            int componentWidth = tooltipComponent.getWidth(this.textRenderer);
            if (componentWidth > maxWidth)
            {
                maxWidth = componentWidth;
            }
            maxHeight += tooltipComponent.getHeight();
        }

        if (x + maxWidth > this.width)
        {
            x -= 28 + maxWidth;
        }

        if (y + maxHeight + 6 > this.height)
        {
            y = this.height - maxHeight - 6;
        }

        matrices.push();
//        this.itemRenderer.zOffset = 400.0f;
//        this.setZOffset(400);
        matrices.translate(0, 0, 400);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        context.fill(x, y, x + maxWidth + 2, y + maxHeight + 2, 0x90000000);
        drawHorizontalLine1(context, x, x + maxWidth + 2, y, col);
        drawHorizontalLine1(context, x, x + maxWidth + 2, y + maxHeight + 2, col);
        drawVerticalLine1(context, x + maxWidth + 2, y, y + maxHeight + 2, col);
        drawVerticalLine1(context, x, y, y + maxHeight + 2, col);

        RenderSystem.disableBlend();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int yAdvance = y + 2;

        for (TooltipComponent tooltipComponent2 : components)
        {
            tooltipComponent2.drawText(this.textRenderer, x + 2, yAdvance, matrix4f, immediate);
            yAdvance += tooltipComponent2.getHeight();
        }

        immediate.draw();
        matrices.pop();
        yAdvance = y;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawItems(this.textRenderer, x, yAdvance, context);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }
    }

    @Override
    public PLCScreenHandler getScreenHandler()
    {
        return handler;
    }

    public PLCScreenEditorState getEditor()
    {
        return editor;
    }

    public PLCScreenShellState getInteractive()
    {
        return shell;
    }

    public boolean passEvents()
    {
        return !editor.isEditFieldFocused();
    }

    abstract class SaveButton extends ClickableWidget
    {
        public SaveButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        public void renderButton(DrawContext matrices, int mouseX, int mouseY, float delta)
        {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, WIDGETS);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int thingHeight = 16;
            matrices.drawTexture(WIDGETS, getX(), getY(), 0, getU(), getV() + i * thingHeight, this.width, this.height, 256, 256);
//            this.renderBackground(matrices, minecraftClient, mouseX, mouseY);

            if (isMouseOver(mouseX, mouseY))
            {
                renderTooltip(matrices, mouseX, mouseY);
            }
        }

        protected int getYImage(boolean hovered)
        {
            return hovered ? 2 : 1;
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0F));
            soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0F));
        }

        protected int getU()
        {
            return 0;
        }

        protected int getV()
        {
            return 0;
        }

        public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
        {
            renderTooltipText(matrices, List.of(getMessage()), true, mouseX, mouseY, PLCCols.BORDER.col);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder)
        {

        }
    }

    class RunButton extends SaveButton
    {
        public RunButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }


        @Override
        protected int getU()
        {
            return handler.isRunning() ? 32 : 16;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            if (handler.isRunning())
                PLCSyncThings.Client.sendPause(plc);
            else
                PLCSyncThings.Client.sendRun(plc);
        }
    }

    class CompileButton extends SaveButton
    {
        public CompileButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        protected int getU()
        {
            return 96;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            PLCSyncThings.Client.sendCompile(plc);
        }
    }

    class StopButton extends SaveButton
    {
        public StopButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        protected int getU()
        {
            return 80;
        }

        @Override
        protected int getYImage(boolean hovered)
        {
            if (handler.hasProgram() > 0)
            {
                if (isHovered())
                    return 2;
                else
                    return 1;
            }
            return 0;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            PLCSyncThings.Client.sendStop(plc);
        }
    }

    class ModeSwitchButton extends SaveButton
    {
        public ModeSwitchButton(int x, int y, int width, int height)
        {
            super(x, y, width, height, Text.empty());
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            mode = RecordMode.cycle(handler.getMode());
            PLCSyncThings.Client.sendMode(plc, mode);
            if (mode == RecordMode.IMMEDIATE)
                state = shell;
            else
                state = editor;

            clearAndInit();
        }

        @Override
        public Text getMessage()
        {
            return switch (handler.getMode())
            {
                case EDIT -> Text.of("Edit Mode");
                case IMMEDIATE -> Text.of("Interactive Mode: Instructions will be executed immediately");
            };
        }

        @Override
        protected int getU()
        {
            return switch (handler.getMode())
            {
                case EDIT -> 64;
                case IMMEDIATE -> 48;
            };
        }
    }

}
