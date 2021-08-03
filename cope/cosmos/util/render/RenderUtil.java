package cope.cosmos.util.render;

import cope.cosmos.util.Wrapper;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderUtil implements Wrapper {

    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder bufferbuilder = RenderUtil.tessellator.getBuffer();
    public static Frustum frustum = new Frustum();

    public static void drawBox(RenderBuilder renderBuilder) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB((double) renderBuilder.getBlockPos().getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double) renderBuilder.getBlockPos().getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double) renderBuilder.getBlockPos().getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double) (renderBuilder.getBlockPos().getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double) (renderBuilder.getBlockPos().getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double) (renderBuilder.getBlockPos().getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);

        switch (renderBuilder.getBox()) {
        case FILL:
            drawSelectionBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor());
            break;

        case OUTLINE:
            drawSelectionBoundingBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 144));
            break;

        case BOTH:
            drawSelectionBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor());
            drawSelectionBoundingBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 144));
            break;

        case GLOW:
            drawSelectionGlowFilledBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 0));
            break;

        case REVERSE:
            drawSelectionGlowFilledBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 0), renderBuilder.getColor());
            break;

        case CLAW:
            drawClawBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 255));
        }

        renderBuilder.build();
    }

    public static void drawSelectionBox(AxisAlignedBB axisAlignedBB, double height, double length, double width, Color color) {
        RenderUtil.bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(RenderUtil.bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + length, axisAlignedBB.maxY + height, axisAlignedBB.maxZ + width, color);
        RenderUtil.tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB axisAlignedBB, double height, double length, double width, Color color) {
        RenderUtil.bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        addChainedBoundingBoxVertices(RenderUtil.bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + length, axisAlignedBB.maxY + height, axisAlignedBB.maxZ + width, color);
        RenderUtil.tessellator.draw();
    }

    public static void addChainedBoundingBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
    }

    public static void drawSelectionGlowFilledBox(AxisAlignedBB axisAlignedBB, double height, double length, double width, Color startColor, Color endColor) {
        RenderUtil.bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        addChainedGlowBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + length, axisAlignedBB.maxY + height, axisAlignedBB.maxZ + width, startColor, endColor);
        RenderUtil.tessellator.draw();
    }

    public static void addChainedGlowBoxVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color startColor, Color endColor) {
        RenderUtil.bufferbuilder.pos(minX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(maxX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, minY, minZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, minY, maxZ).color((float) startColor.getRed() / 255.0F, (float) startColor.getGreen() / 255.0F, (float) startColor.getBlue() / 255.0F, (float) startColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, maxZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
        RenderUtil.bufferbuilder.pos(minX, maxY, minZ).color((float) endColor.getRed() / 255.0F, (float) endColor.getGreen() / 255.0F, (float) endColor.getBlue() / 255.0F, (float) endColor.getAlpha() / 255.0F).endVertex();
    }

    public static void drawClawBox(AxisAlignedBB axisAlignedBB, double height, double length, double width, Color color) {
        RenderUtil.bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        addChainedClawBoxVertices(RenderUtil.bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + length, axisAlignedBB.maxY + height, axisAlignedBB.maxZ + width, color);
        RenderUtil.tessellator.draw();
    }

    public static void addChainedClawBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY, maxZ - 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY, minZ + 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY, maxZ - 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY, minZ + 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX - 0.8D, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX - 0.8D, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX + 0.8D, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX + 0.8D, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY + 0.2D, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, minY + 0.2D, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY + 0.2D, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, minY + 0.2D, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, maxY, maxZ - 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, maxY, minZ + 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, maxY, maxZ - 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, maxY, minZ + 0.8D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX - 0.8D, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX - 0.8D, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX + 0.8D, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX + 0.8D, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, maxY - 0.2D, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(minX, maxY - 0.2D, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, maxY - 0.2D, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        buffer.pos(maxX, maxY - 0.2D, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawCircle(RenderBuilder renderBuilder, Vec3d vec3d, double radius, double height, Color color) {
        renderCircle(RenderUtil.bufferbuilder, vec3d, radius, height, color);
        renderBuilder.build();
    }

    public static void renderCircle(BufferBuilder buffer, Vec3d vec3d, double radius, double height, Color color) {
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        RenderUtil.bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < 361; ++i) {
            buffer.pos(vec3d.x + Math.sin(Math.toRadians((double) i)) * radius - RenderUtil.mc.getRenderManager().viewerPosX, vec3d.y + height - RenderUtil.mc.getRenderManager().viewerPosY, vec3d.z + Math.cos(Math.toRadians((double) i)) * radius - RenderUtil.mc.getRenderManager().viewerPosZ).color((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 1.0F).endVertex();
        }

        RenderUtil.tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
    }

    public static void drawNametag(BlockPos blockPos, float height, String text) {
        GlStateManager.pushMatrix();
        glBillboardDistanceScaled((float) blockPos.getX() + 0.5F, (float) blockPos.getY() + height, (float) blockPos.getZ() + 0.5F, RenderUtil.mc.player, 1.0F);
        GlStateManager.disableDepth();
        GlStateManager.translate(-((double) RenderUtil.mc.fontRenderer.getStringWidth(text) / 2.0D), 0.0D, 0.0D);
        FontUtil.drawStringWithShadow(text, 0.0F, 0.0F, -1);
        GlStateManager.popMatrix();
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance((double) x, (double) y, (double) z);
        float scaleDistance = (float) distance / 2.0F / (2.0F + (2.0F - scale));

        if (scaleDistance < 1.0F) {
            scaleDistance = 1.0F;
        }

        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667F;

        GlStateManager.translate((double) x - RenderUtil.mc.getRenderManager().viewerPosX, (double) y - RenderUtil.mc.getRenderManager().viewerPosY, (double) z - RenderUtil.mc.getRenderManager().viewerPosZ);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-RenderUtil.mc.player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(RenderUtil.mc.player.rotationPitch, RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        Color c = new Color(color, true);

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, (float) c.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawBorderRect(float x, float y, float width, float height, Color color, Color borderColor) {
        drawRect(x, y, width, height, color);
        drawBorder(x, y, width, height, borderColor);
    }

    public static void drawGradientVerticalRect(float x, float y, float width, float height, int topColor, int bottomColor) {
        Color top = new Color(topColor, true);
        Color bottom = new Color(bottomColor, true);

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) top.getRed() / 255.0F, (float) top.getGreen() / 255.0F, (float) top.getBlue() / 255.0F, (float) top.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y);
        GL11.glColor4f((float) bottom.getRed() / 255.0F, (float) bottom.getGreen() / 255.0F, (float) bottom.getBlue() / 255.0F, (float) bottom.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glColor4f((float) top.getRed() / 255.0F, (float) top.getGreen() / 255.0F, (float) top.getBlue() / 255.0F, (float) top.getAlpha() / 255.0F);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawGradientVerticalRect(float x, float y, float width, float height, Color topColor, Color bottomColor) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) topColor.getRed() / 255.0F, (float) topColor.getGreen() / 255.0F, (float) topColor.getBlue() / 255.0F, (float) topColor.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y);
        GL11.glColor4f((float) bottomColor.getRed() / 255.0F, (float) bottomColor.getGreen() / 255.0F, (float) bottomColor.getBlue() / 255.0F, (float) bottomColor.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glColor4f((float) topColor.getRed() / 255.0F, (float) topColor.getGreen() / 255.0F, (float) topColor.getBlue() / 255.0F, (float) topColor.getAlpha() / 255.0F);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawGradientHorizontalRect(float x, float y, float width, float height, int rightColor, int leftColor) {
        Color right = new Color(rightColor, true);
        Color left = new Color(leftColor, true);

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) right.getRed() / 255.0F, (float) right.getGreen() / 255.0F, (float) right.getBlue() / 255.0F, (float) right.getAlpha() / 255.0F);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glColor4f((float) left.getRed() / 255.0F, (float) left.getGreen() / 255.0F, (float) left.getBlue() / 255.0F, (float) left.getAlpha() / 255.0F);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawGradientHorizontalRect(float x, float y, float width, float height, Color right, Color left) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) right.getRed() / 255.0F, (float) right.getGreen() / 255.0F, (float) right.getBlue() / 255.0F, 0.0F);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glColor4f((float) left.getRed() / 255.0F, (float) left.getGreen() / 255.0F, (float) left.getBlue() / 255.0F, 0.0F);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawBorder(float x, float y, float width, float height, int color) {
        drawRect(x - 1.0F, y - 1.0F, 1.0F, height + 2.0F, color);
        drawRect(x + width, y - 1.0F, 1.0F, height + 2.0F, color);
        drawRect(x, y - 1.0F, width, 1.0F, color);
        drawRect(x, y + height, width, 1.0F, color);
    }

    public static void drawBorder(float x, float y, float width, float height, Color color) {
        drawRect(x - 1.0F, y - 1.0F, 1.0F, height + 2.0F, color);
        drawRect(x + width, y - 1.0F, 1.0F, height + 2.0F, color);
        drawRect(x, y - 1.0F, width, 1.0F, color);
        drawRect(x, y + height, width, 1.0F, color);
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        width *= 2.0D;
        height *= 2.0D;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
        GL11.glEnable(2848);
        GL11.glBegin(9);

        int i;

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D);
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(x + radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D, height - radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D);
        }

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(width - radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius, height - radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius);
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
    }

    public static void drawHalfRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        width *= 2.0D;
        height *= 2.0D;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
        GL11.glEnable(2848);
        GL11.glBegin(9);

        int i;

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius * -1.0D);
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(x + 1.0D + Math.sin((double) i * 3.141592653589793D / 180.0D) * -1.0D, height - 1.0D + Math.cos((double) i * 3.141592653589793D / 180.0D) * -1.0D);
        }

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(width - 1.0D + Math.sin((double) i * 3.141592653589793D / 180.0D), height - 1.0D + Math.cos((double) i * 3.141592653589793D / 180.0D));
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin((double) i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double) i * 3.141592653589793D / 180.0D) * radius);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
    }

    public static void drawPolygon(double x, double y, float radius, int sides, Color color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
        RenderUtil.bufferbuilder.begin(6, DefaultVertexFormats.POSITION);
        RenderUtil.bufferbuilder.pos(x, y, 0.0D).endVertex();
        double TWICE_PI = 6.283185307179586D;

        for (int i = 0; i <= sides; ++i) {
            double angle = TWICE_PI * (double) i / (double) sides + Math.toRadians(180.0D);

            RenderUtil.bufferbuilder.pos(x + Math.sin(angle) * (double) radius, y + Math.cos(angle) * (double) radius, 0.0D).endVertex();
        }

        RenderUtil.tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawTriangle(float x, float y, float size, float theta, int color) {
        GL11.glTranslated((double) x, (double) y, 0.0D);
        GL11.glRotatef(180.0F + theta, 0.0F, 0.0F, 1.0F);
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(6);
        GL11.glVertex2d(0.0D, (double) (1.0F * size));
        GL11.glVertex2d((double) (1.0F * size), (double) (-(1.0F * size)));
        GL11.glVertex2d((double) (-(1.0F * size)), (double) (-(1.0F * size)));
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glRotatef(-180.0F - theta, 0.0F, 0.0F, 1.0F);
        GL11.glTranslated((double) (-x), (double) (-y), 0.0D);
    }

    public static void scissor(int x, int y, int x2, int y2) {
        GL11.glScissor(x * (new ScaledResolution(RenderUtil.mc)).getScaleFactor(), ((new ScaledResolution(RenderUtil.mc)).getScaledHeight() - y2) * (new ScaledResolution(RenderUtil.mc)).getScaleFactor(), (x2 - x) * (new ScaledResolution(RenderUtil.mc)).getScaleFactor(), (y2 - y) * (new ScaledResolution(RenderUtil.mc)).getScaleFactor());
    }
}
