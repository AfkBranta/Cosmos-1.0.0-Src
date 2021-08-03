package cope.cosmos.util.render;

import cope.cosmos.client.features.modules.client.Font;
import cope.cosmos.client.font.FontRenderer;
import cope.cosmos.util.Wrapper;
import java.io.InputStream;

public class FontUtil implements Wrapper {

    private static FontRenderer globalFont;

    public static void load() {
        FontUtil.globalFont = new FontRenderer(getFont("hindmadurai", 40.0F));
    }

    public static void drawStringWithShadow(String text, float x, float y, int color) {
        if (Font.INSTANCE.isEnabled()) {
            FontUtil.globalFont.drawStringWithShadow(text, x, y, color);
        } else {
            FontUtil.mc.fontRenderer.drawStringWithShadow(text, x, y, color);
        }

    }

    public static int getStringWidth(String text) {
        return Font.INSTANCE.isEnabled() ? FontUtil.globalFont.getStringWidth(text) : FontUtil.mc.fontRenderer.getStringWidth(text);
    }

    public static int getFontHeight() {
        return Font.INSTANCE.isEnabled() ? FontUtil.globalFont.FONT_HEIGHT : FontUtil.mc.fontRenderer.FONT_HEIGHT;
    }

    public static int getFontString(String text, float x, float y, int color) {
        return Font.INSTANCE.isEnabled() ? FontUtil.globalFont.drawStringWithShadow(text, x, y, color) : FontUtil.mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    private static java.awt.Font getFont(String fontName, float size) {
        try {
            InputStream exception = FontUtil.class.getResourceAsStream("/assets/cosmos/fonts/" + fontName + ".ttf");
            java.awt.Font awtClientFont = java.awt.Font.createFont(0, exception);

            awtClientFont = awtClientFont.deriveFont(0, size);
            exception.close();
            return awtClientFont;
        } catch (Exception exception) {
            exception.printStackTrace();
            return new java.awt.Font("default", 0, (int) size);
        }
    }
}
