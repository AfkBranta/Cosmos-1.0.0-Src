package cope.cosmos.client.clickgui.cosmos.navigation.navs;

import cope.cosmos.client.clickgui.cosmos.navigation.Navigation;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class PlayerNavigation extends Navigation implements Wrapper, Util {

    public void drawNavigation() {
        this.SCREEN_WIDTH = (float) (new ScaledResolution(PlayerNavigation.mc)).getScaledWidth();
        this.SCREEN_HEIGHT = (float) (new ScaledResolution(PlayerNavigation.mc)).getScaledHeight();
        GL11.glPushMatrix();
        RenderUtil.drawRect(0.0F, 0.0F, this.SCREEN_WIDTH, 30.0F, new Color(23, 23, 29));
        float scaledWidth = (float) (FontUtil.getStringWidth(PlayerNavigation.mc.player.getName()) + 8) * 2.75F;

        RenderUtil.drawRect(0.0F, 0.0F, scaledWidth, 40.0F, new Color(23, 23, 29));
        RenderUtil.drawRect(0.0F, 40.0F, scaledWidth, 3.0F, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
        GL11.glScaled(2.75D, 2.75D, 2.75D);
        float scaledX = 2.5454545F;
        float scaledY = 3.2727275F;

        FontUtil.drawStringWithShadow(PlayerNavigation.mc.player.getName(), scaledX, scaledY, (new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue())).getRGB());
        GL11.glScaled(0.3636363744735718D, 0.3636363744735718D, 0.3636363744735718D);
        GL11.glScaled(1.35D, 1.35D, 1.35D);
        scaledX = (this.SCREEN_WIDTH - (float) FontUtil.getStringWidth("GUI") - 25.0F) * 0.7407407F;
        scaledY = 5.185185F;
        FontUtil.drawStringWithShadow("GUI", scaledX, scaledY, -1);
        GL11.glScaled(0.74074074D, 0.74074074D, 0.74074074D);
        RenderUtil.drawRect(this.SCREEN_WIDTH - (float) FontUtil.getStringWidth("GUI") * 1.35F - 21.5F, 20.0F, (float) FontUtil.getStringWidth("GUI") * 1.35F + 3.0F, 2.0F, Color.WHITE);
        GL11.glPopMatrix();
    }

    public void handleLeftClick(int mouseX, int mouseY) {}

    public void handleRightClick(int mouseX, int mouseY) {}

    public void handleKeyPress(char typedCharacter, int key) {}

    public void handleScroll(int scroll) {}
}
