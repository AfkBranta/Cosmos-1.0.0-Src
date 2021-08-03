package cope.cosmos.util.system;

import cope.cosmos.util.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class ProgressUtil implements Wrapper {

    public static void drawSplash(TextureManager textureManager) {
        ScaledResolution scaledresolution = new ScaledResolution(ProgressUtil.mc);
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * scaledresolution.getScaleFactor(), scaledresolution.getScaledHeight() * scaledresolution.getScaleFactor(), true);

        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double) scaledresolution.getScaledWidth(), (double) scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        textureManager.bindTexture(new ResourceLocation("cosmos", "textures/splash.jpg"));
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        Tessellator.getInstance().getBuffer().pos(0.0D, (double) ProgressUtil.mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        Tessellator.getInstance().getBuffer().pos((double) ProgressUtil.mc.displayWidth, (double) ProgressUtil.mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        Tessellator.getInstance().getBuffer().pos((double) ProgressUtil.mc.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        Tessellator.getInstance().getBuffer().pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ProgressUtil.mc.draw((scaledresolution.getScaledWidth() - 256) / 2, (scaledresolution.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * scaledresolution.getScaleFactor(), scaledresolution.getScaledHeight() * scaledresolution.getScaleFactor());
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        ProgressUtil.mc.updateDisplay();
    }
}
