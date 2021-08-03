package cope.cosmos.client.features.modules.client;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.ModuleManager;
import cope.cosmos.client.manager.managers.TickManager;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.system.MathUtil;
import java.awt.Color;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HUD extends Module {

    public static HUD INSTANCE;
    public static Setting hudColor = new Setting("HUDColor", "The primary color for the GUI", new Color(151, 0, 206, 255));
    public static Setting watermark = new Setting("Watermark", "Displays a client watermark", Boolean.valueOf(true));
    public static Setting activeModules = new Setting("ActiveModules", "Displays all enabled modules", Boolean.valueOf(true));
    public static Setting coordinates = new Setting("Coordinates", "Displays the user\'s coordinates", Boolean.valueOf(true));
    public static Setting speed = new Setting("Speed", "Displays the user\'s speed", Boolean.valueOf(true));
    public static Setting ping = new Setting("Ping", "Displays the user\'s server connection speed", Boolean.valueOf(true));
    public static Setting fps = new Setting("FPS", "Displays the current FPS", Boolean.valueOf(true));
    public static Setting tps = new Setting("TPS", "Displays the server TPS", Boolean.valueOf(true));
    float offset;

    public HUD() {
        super("HUD", Category.CLIENT, "Displays the HUD");
        this.setDrawn(false);
        this.setExempt(true);
        HUD.INSTANCE = this;
    }

    public void onRender2d() {
        int SCREEN_WIDTH = (new ScaledResolution(HUD.mc)).getScaledWidth();
        int SCREEN_HEIGHT = (new ScaledResolution(HUD.mc)).getScaledHeight();

        if (((Boolean) HUD.watermark.getValue()).booleanValue()) {
            FontUtil.drawStringWithShadow("Cosmos" + TextFormatting.WHITE + " 1.0.0", 2.0F, 2.0F, ((Color) HUD.hudColor.getValue()).getRGB());
        }

        if (((Boolean) HUD.activeModules.getValue()).booleanValue()) {
            this.offset = 0.0F;
            ModuleManager.getAllModules().stream().filter(Module::isDrawn).filter((module) -> {
                return module.getAnimation().getAnimationFactor() > 0.05D;
            }).sorted(Comparator.comparing((module) -> {
                return Integer.valueOf(FontUtil.getStringWidth(module.getName() + (!module.getInfo().equals("") ? " " + module.getInfo() : "")) * -1);
            })).forEach((module) -> {
                // $FF: Couldn't be decompiled
            });
        }

        if (((Boolean) HUD.speed.getValue()).booleanValue()) {
            double overWorldCoords = HUD.mc.player.posX - HUD.mc.player.prevPosX;
            double distanceZ = HUD.mc.player.posZ - HUD.mc.player.prevPosZ;
            String speedDisplay = "Speed " + TextFormatting.WHITE + MathUtil.roundFloat((double) (MathHelper.sqrt(Math.pow(overWorldCoords, 2.0D) + Math.pow(distanceZ, 2.0D)) / 1000.0F / 1.3888889E-5F), 1) + " kmh";

            FontUtil.drawStringWithShadow(speedDisplay, (float) (SCREEN_WIDTH - FontUtil.getStringWidth(speedDisplay) - 2), (float) (SCREEN_HEIGHT - 10), ((Color) HUD.hudColor.getValue()).getRGB());
        }

        String overWorldCoords1;

        if (((Boolean) HUD.ping.getValue()).booleanValue()) {
            overWorldCoords1 = "Ping " + TextFormatting.WHITE + (!HUD.mc.isSingleplayer() ? ((NetHandlerPlayClient) Objects.requireNonNull(HUD.mc.getConnection())).getPlayerInfo(HUD.mc.player.getUniqueID()).getResponseTime() : 0) + "ms";
            FontUtil.drawStringWithShadow(overWorldCoords1, (float) (SCREEN_WIDTH - FontUtil.getStringWidth(overWorldCoords1) - 2), (float) (SCREEN_HEIGHT - HUD.mc.fontRenderer.FONT_HEIGHT - 11), ((Color) HUD.hudColor.getValue()).getRGB());
        }

        if (((Boolean) HUD.tps.getValue()).booleanValue()) {
            overWorldCoords1 = "TPS " + TextFormatting.WHITE + Cosmos.INSTANCE.getTickManager().getTPS(TickManager.TPS.AVERAGE);
            FontUtil.drawStringWithShadow(overWorldCoords1, (float) (SCREEN_WIDTH - FontUtil.getStringWidth(overWorldCoords1) - 2), (float) (SCREEN_HEIGHT - 2 * HUD.mc.fontRenderer.FONT_HEIGHT - 12), ((Color) HUD.hudColor.getValue()).getRGB());
        }

        if (((Boolean) HUD.fps.getValue()).booleanValue()) {
            overWorldCoords1 = "FPS " + TextFormatting.WHITE + Minecraft.getDebugFPS();
            FontUtil.drawStringWithShadow(overWorldCoords1, (float) (SCREEN_WIDTH - FontUtil.getStringWidth(overWorldCoords1) - 2), (float) (SCREEN_HEIGHT - 3 * HUD.mc.fontRenderer.FONT_HEIGHT - 13), ((Color) HUD.hudColor.getValue()).getRGB());
        }

        if (((Boolean) HUD.coordinates.getValue()).booleanValue()) {
            overWorldCoords1 = HUD.mc.player.dimension != -1 ? "XYZ " + TextFormatting.WHITE + MathUtil.roundFloat(HUD.mc.player.posX, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posY, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posZ, 1) : TextFormatting.GRAY + "XYZ " + TextFormatting.WHITE + MathUtil.roundFloat(HUD.mc.player.posX * 8.0D, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posY * 8.0D, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posZ * 8.0D, 1);
            String netherCoords = HUD.mc.player.dimension == -1 ? "XYZ " + TextFormatting.WHITE + MathUtil.roundFloat(HUD.mc.player.posX, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posY, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posZ, 1) : TextFormatting.RED + "XYZ " + TextFormatting.WHITE + MathUtil.roundFloat(HUD.mc.player.posX / 8.0D, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posY / 8.0D, 1) + " " + MathUtil.roundFloat(HUD.mc.player.posZ / 8.0D, 1);

            FontUtil.drawStringWithShadow(overWorldCoords1, 2.0F, (float) (SCREEN_HEIGHT - 10), ((Color) HUD.hudColor.getValue()).getRGB());
            FontUtil.drawStringWithShadow(netherCoords, 2.0F, (float) (SCREEN_HEIGHT - HUD.mc.fontRenderer.FONT_HEIGHT - 11), (new Color(255, 0, 0)).getRGB());
        }

    }
}
