package cope.cosmos.client.clickgui.cosmos.navigation.navs;

import cope.cosmos.client.clickgui.cosmos.navigation.Navigation;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ControlNavigation extends Navigation implements Wrapper, Util {

    private int hoverAnimation = 0;
    public AnimationManager animationManager = new AnimationManager(300, false);
    public boolean open = false;

    public void drawNavigation() {
        this.SCREEN_WIDTH = (float) (new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth();
        this.SCREEN_HEIGHT = (float) (new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight();
        GL11.glPushMatrix();
        float halfWidth = this.SCREEN_WIDTH / 2.0F;

        if (this.mouseOver(halfWidth - 15.0F, 25.0F, 30.0F, 20.0F) && this.hoverAnimation < 5) {
            ++this.hoverAnimation;
        } else if (!this.mouseOver(halfWidth - 15.0F, 25.0F, 30.0F, 20.0F) && this.hoverAnimation > 0) {
            --this.hoverAnimation;
        }

        RenderUtil.drawRect(0.0F, this.SCREEN_HEIGHT - 30.0F, this.SCREEN_WIDTH, 30.0F, new Color(23, 23, 29));
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        ControlNavigation.mc.getTextureManager().bindTexture(new ResourceLocation("cosmos", "textures/imgs/logotransparent.png"));
        GuiScreen.drawModalRectWithCustomSizedTexture(2, (int) (this.SCREEN_HEIGHT - 28.0F), 0.0F, 0.0F, 104, 26, 104.0F, 26.0F);
        GL11.glPopMatrix();
    }

    public void handleLeftClick(int mouseX, int mouseY) {}

    public void handleRightClick(int mouseX, int mouseY) {}

    public void handleKeyPress(char typedCharacter, int key) {}

    public void handleScroll(int scroll) {}
}
