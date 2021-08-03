package cope.cosmos.client.font;

import cope.cosmos.util.Wrapper;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FontRenderer extends net.minecraft.client.gui.FontRenderer implements Wrapper {

    private final ImageAWT defaultFont;

    public FontRenderer(Font font) {
        super(FontRenderer.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), FontRenderer.mc.getTextureManager(), false);
        this.defaultFont = new ImageAWT(font);
        this.FONT_HEIGHT = this.getHeight();
    }

    public int getHeight() {
        return this.defaultFont.getHeight() / 2;
    }

    public int getSize() {
        return this.defaultFont.getFont().getSize();
    }

    @ParametersAreNonnullByDefault
    public int drawStringWithShadow(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, true);
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        float currY = y - 3.0F;

        if (!text.contains("\n")) {
            if (dropShadow) {
                this.drawText(text, x + 0.4F, currY + 0.3F, (new Color(0, 0, 0, 150)).getRGB(), true);
            }

            return this.drawText(text, x, currY, color, false);
        } else {
            String[] parts = text.split("\n");
            float newY = 0.0F;
            String[] astring = parts;
            int i = parts.length;

            for (int j = 0; j < i; ++j) {
                String s = astring[j];

                this.drawText(s, x, currY + newY, color, dropShadow);
                newY += (float) this.getHeight();
            }

            return 0;
        }
    }

    private int drawText(String text, float x, float y, int color, boolean ignoreColor) {
        if (text == null) {
            return 0;
        } else if (text.isEmpty()) {
            return (int) x;
        } else {
            GlStateManager.translate((double) x - 1.5D, (double) y + 0.5D, 0.0D);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.enableTexture2D();
            GL11.glEnable(2848);
            int currentColor = color;

            if ((color & -67108864) == 0) {
                currentColor = color | -16777216;
            }

            int alpha = currentColor >> 24 & 255;

            if (text.contains("§")) {
                String[] parts = text.split("§");
                ImageAWT currentFont = this.defaultFont;
                double width = 0.0D;
                boolean randomCase = false;

                for (int index = 0; index < parts.length; ++index) {
                    String part = parts[index];

                    if (!part.isEmpty()) {
                        if (index == 0) {
                            currentFont.drawString(part, width, 0.0D, currentColor);
                            width += (double) currentFont.getStringWidth(part);
                        } else {
                            String words = part.substring(1);
                            char type = part.charAt(0);
                            int colorIndex = "0123456789abcdefklmnor".indexOf(type);

                            switch (colorIndex) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                                if (!ignoreColor) {
                                    currentColor = FontRenderer.ColorUtils.hexColors[colorIndex] | alpha << 24;
                                }

                                randomCase = false;
                                break;

                            case 16:
                                randomCase = true;

                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            default:
                                break;

                            case 21:
                                currentColor = color;
                                if ((color & -67108864) == 0) {
                                    currentColor = color | -16777216;
                                }

                                randomCase = false;
                            }

                            currentFont = this.defaultFont;
                            if (randomCase) {
                                currentFont.drawString(FontRenderer.ColorUtils.randomMagicText(words), width, 0.0D, currentColor);
                            } else {
                                currentFont.drawString(words, width, 0.0D, currentColor);
                            }

                            width += (double) currentFont.getStringWidth(words);
                        }
                    }
                }
            } else {
                this.defaultFont.drawString(text, 0.0D, 0.0D, currentColor);
            }

            GL11.glDisable(2848);
            GlStateManager.disableBlend();
            GlStateManager.translate(-((double) x - 1.5D), -((double) y + 0.5D), 0.0D);
            return (int) (x + (float) this.getStringWidth(text));
        }
    }

    public int getColorCode(char charCode) {
        return FontRenderer.ColorUtils.hexColors[getColorIndex(charCode)];
    }

    public int getStringWidth(String text) {
        if (text.contains("§")) {
            String[] parts = text.split("§");
            ImageAWT currentFont = this.defaultFont;
            int width = 0;

            for (int index = 0; index < parts.length; ++index) {
                String part = parts[index];

                if (!part.isEmpty()) {
                    if (index == 0) {
                        width += currentFont.getStringWidth(part);
                    } else {
                        String words = part.substring(1);

                        currentFont = this.defaultFont;
                        width += currentFont.getStringWidth(words);
                    }
                }
            }

            return width / 2;
        } else {
            return this.defaultFont.getStringWidth(text) / 2;
        }
    }

    public int getCharWidth(char character) {
        return this.getStringWidth(String.valueOf(character));
    }

    @ParametersAreNonnullByDefault
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @ParametersAreNonnullByDefault
    protected void bindTexture(ResourceLocation location) {}

    public static int getColorIndex(char type) {
        switch (type) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return type - 48;

        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '_':
        case '`':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'p':
        case 'q':
        default:
            return -1;

        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
            return type - 97 + 10;

        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
            return type - 107 + 16;

        case 'r':
            return 21;
        }
    }

    private static class ColorUtils {

        public static int[] hexColors = new int[16];
        private static final Random random;
        private static final String magicAllowedCharacters = "À�?ÂÈÊË�?ÓÔÕÚßãõğİıŒœŞşŴŵžȇ !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗�?╜╛�?└┴┬├─┼╞╟╚╔╩╦╠�?╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌�?▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√�?�²■";

        public static String randomMagicText(String text) {
            StringBuilder stringBuilder = new StringBuilder();
            char[] achar = text.toCharArray();
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char ch = achar[j];

                if (ChatAllowedCharacters.isAllowedCharacter(ch)) {
                    int index = FontRenderer.ColorUtils.random.nextInt("À�?ÂÈÊË�?ÓÔÕÚßãõğİıŒœŞşŴŵžȇ !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗�?╜╛�?└┴┬├─┼╞╟╚╔╩╦╠�?╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌�?▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√�?�²■".length());

                    stringBuilder.append("À�?ÂÈÊË�?ÓÔÕÚßãõğİıŒœŞşŴŵžȇ !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗�?╜╛�?└┴┬├─┼╞╟╚╔╩╦╠�?╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌�?▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√�?�²■".charAt(index));
                }
            }

            return stringBuilder.toString();
        }

        static {
            FontRenderer.ColorUtils.hexColors[0] = 0;
            FontRenderer.ColorUtils.hexColors[1] = 170;
            FontRenderer.ColorUtils.hexColors[2] = 'ꨀ';
            FontRenderer.ColorUtils.hexColors[3] = 'ꪪ';
            FontRenderer.ColorUtils.hexColors[4] = 11141120;
            FontRenderer.ColorUtils.hexColors[5] = 11141290;
            FontRenderer.ColorUtils.hexColors[6] = 16755200;
            FontRenderer.ColorUtils.hexColors[7] = 11184810;
            FontRenderer.ColorUtils.hexColors[8] = 5592405;
            FontRenderer.ColorUtils.hexColors[9] = 5592575;
            FontRenderer.ColorUtils.hexColors[10] = 5635925;
            FontRenderer.ColorUtils.hexColors[11] = 5636095;
            FontRenderer.ColorUtils.hexColors[12] = 16733525;
            FontRenderer.ColorUtils.hexColors[13] = 16733695;
            FontRenderer.ColorUtils.hexColors[14] = 16777045;
            FontRenderer.ColorUtils.hexColors[15] = 16777215;
            random = new Random();
        }
    }
}
