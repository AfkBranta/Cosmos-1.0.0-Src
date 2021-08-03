package cope.cosmos.util.render;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class RenderBuilder {

    private boolean setup = false;
    private boolean depth = false;
    private boolean blend = false;
    private boolean texture = false;
    private boolean cull = false;
    private boolean alpha = false;
    private boolean shade = false;
    private BlockPos blockPos;
    private double height;
    private double length;
    private double width;
    private Color color;
    private RenderBuilder.Box box;

    public RenderBuilder() {
        this.blockPos = BlockPos.ORIGIN;
        this.height = 0.0D;
        this.length = 0.0D;
        this.width = 0.0D;
        this.color = new Color(255, 255, 255, 255);
        this.box = RenderBuilder.Box.FILL;
    }

    public RenderBuilder setup() {
        GlStateManager.pushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        this.setup = true;
        return this;
    }

    public RenderBuilder depth(boolean depth) {
        if (depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }

        this.depth = depth;
        return this;
    }

    public RenderBuilder blend() {
        GlStateManager.enableBlend();
        this.blend = true;
        return this;
    }

    public RenderBuilder texture() {
        GlStateManager.disableTexture2D();
        this.texture = true;
        return this;
    }

    public RenderBuilder line(float width) {
        GL11.glLineWidth(width);
        return this;
    }

    public RenderBuilder cull(boolean cull) {
        if (cull) {
            GlStateManager.disableCull();
        }

        this.cull = cull;
        return this;
    }

    public RenderBuilder alpha(boolean alpha) {
        if (alpha) {
            GlStateManager.disableAlpha();
        }

        this.alpha = alpha;
        return this;
    }

    public RenderBuilder shade(boolean shade) {
        if (shade) {
            GlStateManager.shadeModel(7425);
        }

        this.shade = shade;
        return this;
    }

    public RenderBuilder build() {
        if (this.depth) {
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }

        if (this.texture) {
            GlStateManager.enableTexture2D();
        }

        if (this.blend) {
            GlStateManager.disableBlend();
        }

        if (this.setup) {
            GL11.glDisable(2848);
            GlStateManager.popMatrix();
        }

        if (this.cull) {
            GlStateManager.enableCull();
        }

        if (this.alpha) {
            GlStateManager.enableAlpha();
        }

        if (this.shade) {
            GlStateManager.shadeModel(7424);
        }

        return this;
    }

    public RenderBuilder position(BlockPos blockPos) {
        this.blockPos = blockPos;
        return this;
    }

    public RenderBuilder height(double height) {
        this.height = height;
        return this;
    }

    public RenderBuilder width(double width) {
        this.width = width;
        return this;
    }

    public RenderBuilder length(double length) {
        this.length = length;
        return this;
    }

    public RenderBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public RenderBuilder box(RenderBuilder.Box box) {
        this.box = box;
        return this;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public double getHeight() {
        return this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getLength() {
        return this.length;
    }

    public Color getColor() {
        return this.color;
    }

    public RenderBuilder.Box getBox() {
        return this.box;
    }

    public static enum Box {

        FILL, OUTLINE, BOTH, GLOW, REVERSE, CLAW, NONE;
    }
}
