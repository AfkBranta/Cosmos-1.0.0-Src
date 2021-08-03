package cope.cosmos.client.features.modules.visual;

import cope.cosmos.client.events.RenderCrystalEvent;
import cope.cosmos.client.events.RenderLivingEntityEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.client.ColorUtil;
import cope.cosmos.util.world.EntityUtil;
import java.awt.Color;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {

    public static Chams INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for Chams", Chams.Mode.MODEL);
    public static Setting width = (new Setting(get<invokedynamic>(), "Width", "Line width for the model", Double.valueOf(0.0D), Double.valueOf(3.0D), Double.valueOf(5.0D), 2)).setParent(Chams.mode);
    public static Setting players = new Setting("Players", "Renders chams on players", Boolean.valueOf(true));
    public static Setting local = (new Setting("Local", "Renders chams on the local player", Boolean.valueOf(false))).setParent(Chams.players);
    public static Setting mobs = new Setting("Mobs", "Renders chams on mobs", Boolean.valueOf(true));
    public static Setting monsters = new Setting("Monsters", "Renders chams on monsters", Boolean.valueOf(true));
    public static Setting crystals = new Setting("Crystals", "Renders chams on crystals", Boolean.valueOf(true));
    public static Setting scale = (new Setting("Scale", "Scale for crystal model", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(2.0D), 2)).setParent(Chams.crystals);
    public static Setting texture = new Setting("Texture", "Enables entity texture", Boolean.valueOf(false));
    public static Setting lighting = new Setting("Lighting", "Disables vanilla lighting", Boolean.valueOf(true));
    public static Setting blend = new Setting("Blend", "Enables blended texture", Boolean.valueOf(false));
    public static Setting transparent = new Setting("Transparent", "Makes entity models transparent", Boolean.valueOf(true));
    public static Setting depth = new Setting("Depth", "Enables entity depth", Boolean.valueOf(true));
    public static Setting walls = new Setting("Walls", "Renders chams models through walls", Boolean.valueOf(true));
    public static Setting xqz = new Setting("XQZ", "Colors chams models through walls", Boolean.valueOf(true));
    public static Setting xqzColor = (new Setting("XQZColor", "Color of models through walls", new Color(0, 70, 250, 50))).setParent(Chams.xqz);
    public static Setting highlight = new Setting("Highlight", "Colors chams models when visible", Boolean.valueOf(true));
    public static Setting highlightColor = (new Setting("HighlightColor", "Color of models when visible", new Color(250, 0, 250, 50))).setParent(Chams.highlight);

    public Chams() {
        super("Chams", Category.VISUAL, "Renders entity models through walls");
        Chams.INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (this.nullCheck() && (event.getEntityLivingBase() instanceof EntityOtherPlayerMP && ((Boolean) Chams.players.getValue()).booleanValue() || event.getEntityLivingBase() instanceof EntityPlayerSP && ((Boolean) Chams.local.getValue()).booleanValue() || (EntityUtil.isPassiveMob(event.getEntityLivingBase()) || EntityUtil.isNeutralMob(event.getEntityLivingBase())) && ((Boolean) Chams.mobs.getValue()).booleanValue() || EntityUtil.isHostileMob(event.getEntityLivingBase()) && ((Boolean) Chams.monsters.getValue()).booleanValue())) {
            event.setCanceled(!((Boolean) Chams.texture.getValue()).booleanValue());
            if (((Boolean) Chams.transparent.getValue()).booleanValue()) {
                GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
            }

            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            if (!((Boolean) Chams.texture.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.SHINE)) {
                GL11.glDisable(3553);
            }

            if (((Boolean) Chams.blend.getValue()).booleanValue()) {
                GL11.glEnable(3042);
            }

            if (((Boolean) Chams.lighting.getValue()).booleanValue()) {
                GL11.glDisable(2896);
            }

            if (((Boolean) Chams.depth.getValue()).booleanValue()) {
                GL11.glDepthMask(false);
            }

            if (((Boolean) Chams.walls.getValue()).booleanValue()) {
                GL11.glDisable(2929);
            }

            switch ((Chams.Mode) Chams.mode.getValue()) {
            case WIRE:
                GL11.glPolygonMode(1032, 6913);
                break;

            case WIREMODEL:
            case MODEL:
                GL11.glPolygonMode(1032, 6914);
            }

            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth((float) ((Double) Chams.width.getValue()).doubleValue());
            if (((Boolean) Chams.xqz.getValue()).booleanValue()) {
                ColorUtil.setColor((Color) Chams.xqzColor.getValue());
            }

            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (((Boolean) Chams.walls.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }

            if (((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glPolygonMode(1032, 6913);
            }

            if (((Boolean) Chams.highlight.getValue()).booleanValue()) {
                ColorUtil.setColor(((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL) ? new Color(((Color) Chams.xqzColor.getValue()).getRed(), ((Color) Chams.xqzColor.getValue()).getGreen(), ((Color) Chams.xqzColor.getValue()).getBlue(), 255) : (Color) Chams.highlightColor.getValue());
            }

            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (((Boolean) Chams.walls.getValue()).booleanValue() && ((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }

            if (((Boolean) Chams.lighting.getValue()).booleanValue()) {
                GL11.glEnable(2896);
            }

            if (((Boolean) Chams.depth.getValue()).booleanValue()) {
                GL11.glDepthMask(true);
            }

            if (((Boolean) Chams.blend.getValue()).booleanValue()) {
                GL11.glDisable(3042);
            }

            if (!((Boolean) Chams.texture.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.SHINE)) {
                GL11.glEnable(3553);
            }

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

    }

    @SubscribeEvent
    public void onRenderCrystalPre(RenderCrystalEvent.RenderCrystalPreEvent event) {
        event.setCanceled(this.nullCheck() && Chams.INSTANCE.isEnabled() && ((Boolean) Chams.crystals.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderCrystalPost(RenderCrystalEvent.RenderCrystalPostEvent event) {
        if (this.nullCheck() && ((Boolean) Chams.crystals.getValue()).booleanValue()) {
            if (((Boolean) Chams.transparent.getValue()).booleanValue()) {
                GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
            }

            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            float rotation = (float) event.getEntityEnderCrystal().innerRotation + event.getPartialTicks();
            float rotationMoved = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;

            rotationMoved = (float) ((double) rotationMoved + Math.pow((double) rotationMoved, 2.0D));
            GL11.glTranslated(event.getX(), event.getY(), event.getZ());
            GL11.glScaled(((Double) Chams.scale.getValue()).doubleValue(), ((Double) Chams.scale.getValue()).doubleValue(), ((Double) Chams.scale.getValue()).doubleValue());
            if (!((Boolean) Chams.texture.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.SHINE)) {
                GL11.glDisable(3553);
            }

            if (((Boolean) Chams.blend.getValue()).booleanValue()) {
                GL11.glEnable(3042);
            }

            if (((Boolean) Chams.lighting.getValue()).booleanValue()) {
                GL11.glDisable(2896);
            }

            if (((Boolean) Chams.depth.getValue()).booleanValue()) {
                GL11.glDepthMask(false);
            }

            if (((Boolean) Chams.walls.getValue()).booleanValue()) {
                GL11.glDisable(2929);
            }

            switch ((Chams.Mode) Chams.mode.getValue()) {
            case WIRE:
                GL11.glPolygonMode(1032, 6913);
                break;

            case WIREMODEL:
            case MODEL:
                GL11.glPolygonMode(1032, 6914);
            }

            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth((float) ((Double) Chams.width.getValue()).doubleValue());
            if (((Boolean) Chams.xqz.getValue()).booleanValue()) {
                ColorUtil.setColor((Color) Chams.xqzColor.getValue());
            }

            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0F, rotation * 3.0F, rotationMoved * 0.2F, 0.0F, 0.0F, 0.0625F);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0F, rotation * 3.0F, rotationMoved * 0.2F, 0.0F, 0.0F, 0.0625F);
            }

            if (((Boolean) Chams.walls.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }

            if (((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glPolygonMode(1032, 6913);
            }

            if (((Boolean) Chams.highlight.getValue()).booleanValue()) {
                ColorUtil.setColor(((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL) ? new Color(((Color) Chams.xqzColor.getValue()).getRed(), ((Color) Chams.xqzColor.getValue()).getGreen(), ((Color) Chams.xqzColor.getValue()).getBlue(), 255) : (Color) Chams.highlightColor.getValue());
            }

            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0F, rotation * 3.0F, rotationMoved * 0.2F, 0.0F, 0.0F, 0.0625F);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0F, rotation * 3.0F, rotationMoved * 0.2F, 0.0F, 0.0F, 0.0625F);
            }

            if (((Boolean) Chams.walls.getValue()).booleanValue() && ((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }

            if (((Boolean) Chams.lighting.getValue()).booleanValue()) {
                GL11.glEnable(2896);
            }

            if (((Boolean) Chams.depth.getValue()).booleanValue()) {
                GL11.glDepthMask(true);
            }

            if (((Boolean) Chams.blend.getValue()).booleanValue()) {
                GL11.glDisable(3042);
            }

            if (!((Boolean) Chams.texture.getValue()).booleanValue() && !((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.SHINE)) {
                GL11.glEnable(3553);
            }

            GL11.glScaled(1.0D / ((Double) Chams.scale.getValue()).doubleValue(), 1.0D / ((Double) Chams.scale.getValue()).doubleValue(), 1.0D / ((Double) Chams.scale.getValue()).doubleValue());
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

    }

    private static Boolean lambda$static$0() {
        return Boolean.valueOf(((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIRE) || ((Chams.Mode) Chams.mode.getValue()).equals(Chams.Mode.WIREMODEL));
    }

    public static enum Mode {

        MODEL, WIRE, WIREMODEL, SHINE;
    }
}
