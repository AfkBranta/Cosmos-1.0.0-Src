package cope.cosmos.util.client;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class ColorUtil {

    public static Color alphaCycle(Color color, int index, int count) {
        float[] hsb = new float[3];

        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);

        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static int makeBrighter(int color) {
        Color c = new Color(color, true);

        return toInt(c.brighter());
    }

    public static int makeDarker(int color) {
        Color c = new Color(color, true);

        return toInt(c.darker());
    }

    public static int toInt(Color color) {
        return (color.getRed() << 16) + (color.getGreen() << 8) + (color.getBlue() << 0) + (color.getAlpha() << 24);
    }

    public static void setColor(Color color) {
        GL11.glColor4d((double) ((float) color.getRed() / 255.0F), (double) ((float) color.getGreen() / 255.0F), (double) ((float) color.getBlue() / 255.0F), (double) ((float) color.getAlpha() / 255.0F));
    }
}
