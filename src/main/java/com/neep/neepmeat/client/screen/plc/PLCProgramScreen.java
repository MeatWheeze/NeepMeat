package com.neep.neepmeat.client.screen.plc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import com.neep.neepmeat.client.screen.StyledTooltipUser;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.plc.PLCSyncAction;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
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

public class PLCProgramScreen extends Screen implements ScreenHandlerProvider<PLCScreenHandler>, StyledTooltipUser
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

        handler.compileMessageS2C.receiver(editor::setCompileMessage);
        handler.updateStackS2C.receiver(handler.getPlc()::updateVariableStack);
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

            addDrawableChild(new ModeSwitchButton(width - 17, 1));
            addDrawableChild(new StopButton(width - 2 * 17, 1, Text.of("Stop")));
            addDrawableChild(new RunButton(width - 3 * 17, 1, Text.of("Run")));
            addDrawableChild(new CompileButton(width - 4 * 17, 1, Text.of("Compile")));
            state = editor;
        }
        else
        {
            addDrawableChild(shell);
            addDrawableChild(new ModeSwitchButton(width - 17, 1));
            addDrawableChild(new StopButton(width - 2 * 17, 1, Text.of("Stop")));
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

                tooltipText.addAll(stack.getTooltip(client.player, TooltipContext.Default.BASIC));
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        var hovered = hoveredElement(mouseX, mouseY);
        return super.mouseScrolled(mouseX, mouseY, amount);
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

    @Override
    public TextRenderer textRenderer()
    {
        return textRenderer;
    }

    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }

    public abstract class BaseButton extends PLCScreenButton
    {
        public BaseButton(int x, int y, Text message)
        {
            super(x, y, message);
        }

        public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
        {
            renderTooltipText(matrices, List.of(getMessage()), true, mouseX, mouseY, PLCCols.BORDER.col);
        }
    }

    class RunButton extends BaseButton
    {
        public RunButton(int x, int y, Text message)
        {
            super(x, y, message);
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
                handler.channel.emitter().apply(PLCSyncAction.PAUSE);
            else
                handler.channel.emitter().apply(PLCSyncAction.RUN);
        }
    }

    class CompileButton extends BaseButton
    {
        public CompileButton(int x, int y, Text message)
        {
            super(x, y, message);
        }

        @Override
        protected int getU()
        {
            return 96;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            handler.channel.emitter().apply(PLCSyncAction.COMPILE);
        }
    }

    class StopButton extends BaseButton
    {
        public StopButton(int x, int y, Text message)
        {
            super(x, y, message);
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
            handler.channel.emitter().apply(PLCSyncAction.STOP);
        }
    }

    class ModeSwitchButton extends BaseButton
    {
        public ModeSwitchButton(int x, int y)
        {
            super(x, y, Text.empty());
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            mode = RecordMode.cycle(handler.getMode());
            handler.changeMode.emitter().accept(mode);
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
