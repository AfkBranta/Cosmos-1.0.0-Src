package cope.cosmos.client.manager.managers;

import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class ProgressManager extends Manager implements Wrapper {

    private static ResourceLocation splash;
    private static TextureManager textureManager;

    public ProgressManager() {
        super("ProgressManager", "Renders the client custom splash screen", 9);
    }

    public static void update() {
        if (ProgressManager.mc != null && ProgressManager.mc.getLanguageManager() != null) {
            drawSplash(ProgressManager.mc.getTextureManager());
        }
    }

    public static void setProgress() {
        update();
    }

    public static void drawSplash(TextureManager mcTextureManager) {
        if (ProgressManager.textureManager == null) {
            ProgressManager.textureManager = mcTextureManager;
        }

        try {
            ScaledResolution scaledresolution = new ScaledResolution(ProgressManager.mc);
            int scaleFactor = scaledresolution.getScaleFactor();
            Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor, true);

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
            if (ProgressManager.splash == null) {
                ProgressManager.splash = new ResourceLocation("cosmos", "textures/imgs/splash.jpg");
            }

            mcTextureManager.bindTexture(ProgressManager.splash);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, 1920, 1080, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 1920.0F, 1080.0F);
            framebuffer.unbindFramebuffer();
            framebuffer.framebufferRender(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            ProgressManager.mc.updateDisplay();
        } catch (Exception exception) {
            ;
        }

    }
}
