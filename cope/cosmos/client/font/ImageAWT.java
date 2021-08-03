package cope.cosmos.client.font;

import cope.cosmos.util.Wrapper;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ImageAWT implements Wrapper {

    private static final ArrayList activeFontRenderers = new ArrayList();
    private static int gcTicks = 0;
    private final Font font;
    private int fontHeight;
    private final ImageAWT.CharLocation[] charLocations;
    private final HashMap cachedStrings;
    private int textureID;
    private int textureWidth;
    private int textureHeight;

    public static void garbageCollectionTick() {
        if (ImageAWT.gcTicks++ > 600) {
            ImageAWT.activeFontRenderers.forEach(accept<invokedynamic>());
            ImageAWT.gcTicks = 0;
        }

    }

    public ImageAWT(Font font, int startChar, int stopChar) {
        this.fontHeight = -1;
        this.cachedStrings = new HashMap();
        this.textureID = 0;
        this.textureWidth = 0;
        this.textureHeight = 0;
        this.font = font;
        this.charLocations = new ImageAWT.CharLocation[stopChar];
        this.renderBitmap(startChar, stopChar);
        ImageAWT.activeFontRenderers.add(this);
    }

    public ImageAWT(Font font) {
        this(font, 0, 255);
    }

    private void collectGarbage() {
        long currentTime = System.currentTimeMillis();

        this.cachedStrings.entrySet().stream().filter(test<invokedynamic>(currentTime)).forEach(accept<invokedynamic>(this));
    }

    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    public void drawString(String text, double x, double y, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.25D, 0.25D, 0.25D);
        GL11.glTranslated(x * 2.0D, y * 2.0D - 2.0D, 0.0D);
        GlStateManager.bindTexture(this.textureID);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        GlStateManager.color(red, green, blue, alpha);
        double currX = 0.0D;
        FontCache cached = (FontCache) this.cachedStrings.get(text);

        if (cached != null) {
            GL11.glCallList(cached.getDisplayList());
            cached.setLastUsage(System.currentTimeMillis());
            GlStateManager.popMatrix();
        } else {
            int list = -1;
            boolean assumeNonVolatile = false;

            if (assumeNonVolatile) {
                list = GL11.glGenLists(1);
                GL11.glNewList(list, 4865);
            }

            GL11.glBegin(7);
            char[] achar = text.toCharArray();
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char ch = achar[j];

                if (Character.getNumericValue(ch) >= this.charLocations.length) {
                    GL11.glEnd();
                    GlStateManager.scale(4.0D, 4.0D, 4.0D);
                    ImageAWT.mc.fontRenderer.drawString(String.valueOf(ch), (float) currX * 0.25F + 1.0F, 2.0F, color, false);
                    currX += (double) ImageAWT.mc.fontRenderer.getStringWidth(String.valueOf(ch)) * 4.0D;
                    GlStateManager.scale(0.25D, 0.25D, 0.25D);
                    GlStateManager.bindTexture(this.textureID);
                    GlStateManager.color(red, green, blue, alpha);
                    GL11.glBegin(7);
                } else {
                    ImageAWT.CharLocation fontChar;

                    if (this.charLocations.length > ch && (fontChar = this.charLocations[ch]) != null) {
                        this.drawChar(fontChar, (float) currX, 0.0F);
                        currX += (double) fontChar.width - 8.0D;
                    }
                }
            }

            GL11.glEnd();
            if (assumeNonVolatile) {
                this.cachedStrings.put(text, new FontCache(list, System.currentTimeMillis()));
                GL11.glEndList();
            }

            GlStateManager.popMatrix();
        }
    }

    private void drawChar(ImageAWT.CharLocation ch, float x, float y) {
        float width = (float) ch.width;
        float height = (float) ch.height;
        float srcX = (float) ch.x;
        float srcY = (float) ch.y;
        float renderX = srcX / (float) this.textureWidth;
        float renderY = srcY / (float) this.textureHeight;
        float renderWidth = width / (float) this.textureWidth;
        float renderHeight = height / (float) this.textureHeight;

        GL11.glTexCoord2f(renderX, renderY);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(renderX, renderY + renderHeight);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(renderX + renderWidth, renderY + renderHeight);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(renderX + renderWidth, renderY);
        GL11.glVertex2f(x + width, y);
    }

    private void renderBitmap(int startChar, int stopChar) {
        BufferedImage[] fontImages = new BufferedImage[stopChar];
        int rowHeight = 0;
        int charX = 0;
        int charY = 0;

        for (int bufferedImage = startChar; bufferedImage < stopChar; ++bufferedImage) {
            BufferedImage graphics2D = this.drawCharToImage((char) bufferedImage);
            ImageAWT.CharLocation targetChar = new ImageAWT.CharLocation(charX, charY, graphics2D.getWidth(), graphics2D.getHeight());

            if (targetChar.height > this.fontHeight) {
                this.fontHeight = targetChar.height;
            }

            if (targetChar.height > rowHeight) {
                rowHeight = targetChar.height;
            }

            if (this.charLocations.length > bufferedImage) {
                this.charLocations[bufferedImage] = targetChar;
                fontImages[bufferedImage] = graphics2D;
                if ((charX += targetChar.width) > 2048) {
                    if (charX > this.textureWidth) {
                        this.textureWidth = charX;
                    }

                    charX = 0;
                    charY += rowHeight;
                    rowHeight = 0;
                }
            }
        }

        this.textureHeight = charY + rowHeight;
        BufferedImage bufferedimage = new BufferedImage(this.textureWidth, this.textureHeight, 2);
        Graphics2D graphics2d = (Graphics2D) bufferedimage.getGraphics();

        graphics2d.setFont(this.font);
        graphics2d.setColor(new Color(255, 255, 255, 0));
        graphics2d.fillRect(0, 0, this.textureWidth, this.textureHeight);
        graphics2d.setColor(Color.WHITE);

        for (int i = startChar; i < stopChar; ++i) {
            if (fontImages[i] != null && this.charLocations[i] != null) {
                graphics2d.drawImage(fontImages[i], this.charLocations[i].x, this.charLocations[i].y, (ImageObserver) null);
            }
        }

        this.textureID = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), bufferedimage, true, true);
    }

    private BufferedImage drawCharToImage(char ch) {
        Graphics2D graphics2D = (Graphics2D) (new BufferedImage(1, 1, 2)).getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(this.font);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int charWidth = fontMetrics.charWidth(ch) + 8;

        if (charWidth <= 8) {
            charWidth = 7;
        }

        int charHeight;

        if ((charHeight = fontMetrics.getHeight() + 3) <= 0) {
            charHeight = this.font.getSize();
        }

        BufferedImage fontImage = new BufferedImage(charWidth, charHeight, 2);
        Graphics2D graphics = (Graphics2D) fontImage.getGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(ch), 3, 1 + fontMetrics.getAscent());
        return fontImage;
    }

    public int getStringWidth(String text) {
        int width = 0;
        char[] achar = text.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char ch = achar[j];
            char index = ch < this.charLocations.length ? ch : 3;
            ImageAWT.CharLocation fontChar;

            if (this.charLocations.length > index && (fontChar = this.charLocations[index]) != null) {
                width += fontChar.width - 8;
            }
        }

        return width / 2;
    }

    public Font getFont() {
        return this.font;
    }

    private void lambda$collectGarbage$1(Entry entry) {
        GL11.glDeleteLists(((FontCache) entry.getValue()).getDisplayList(), 1);
        ((FontCache) entry.getValue()).setDeleted(true);
        this.cachedStrings.remove(entry.getKey());
    }

    private static boolean lambda$collectGarbage$0(long currentTime, Entry entry) {
        return currentTime - ((FontCache) entry.getValue()).getLastUsage() > 30000L;
    }

    private static class CharLocation {

        private final int x;
        private final int y;
        private final int width;
        private final int height;

        CharLocation(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
