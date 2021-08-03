package cope.cosmos.client.features.modules.visual;

import cope.cosmos.client.events.BossOverlayEvent;
import cope.cosmos.client.events.HurtCameraEvent;
import cope.cosmos.client.events.LayerArmorEvent;
import cope.cosmos.client.events.ModifyFOVEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.RenderBeaconBeamEvent;
import cope.cosmos.client.events.RenderEnchantmentTableBookEvent;
import cope.cosmos.client.events.RenderMapEvent;
import cope.cosmos.client.events.RenderSkylightEvent;
import cope.cosmos.client.events.RenderWitherSkullEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.PlayerUtil;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {

    public static NoRender INSTANCE;
    public static Setting overlays = new Setting("Overlays", "Prevents overlays from rendering", Boolean.valueOf(true));
    public static Setting overlayFire = (new Setting("Fire", "Prevents fire overlay from rendering", Boolean.valueOf(true))).setParent(NoRender.overlays);
    public static Setting overlayLiquid = (new Setting("Liquid", "Prevents liquid overlay from rendering", Boolean.valueOf(true))).setParent(NoRender.overlays);
    public static Setting overlayBlock = (new Setting("Block", "Prevents block overlay from rendering", Boolean.valueOf(true))).setParent(NoRender.overlays);
    public static Setting overlayBoss = (new Setting("Boss", "Prevents boss bar overlay from rendering", Boolean.valueOf(true))).setParent(NoRender.overlays);
    public static Setting fog = new Setting("Fog", "Prevents fog from rendering", Boolean.valueOf(true));
    public static Setting fogLiquid = (new Setting("LiquidVision", "Clears fog in liquid", Boolean.valueOf(true))).setParent(NoRender.fog);
    public static Setting fogDensity = (new Setting("Density", "Density of the fog", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(20.0D), 0)).setParent(NoRender.fog);
    public static Setting armor = new Setting("Armor", "Prevents armor from rendering", Boolean.valueOf(true));
    public static Setting items = new Setting("Items", "Prevents dropped items from rendering", Boolean.valueOf(false));
    public static Setting particles = new Setting("Particles", "Prevents laggy particles from rendering", Boolean.valueOf(false));
    public static Setting tileEntities = new Setting("TileEntities", "Prevents tile entity effects (enchantment table books, beacon beams, etc.) from rendering", Boolean.valueOf(false));
    public static Setting maps = new Setting("Maps", "Prevents maps from rendering", Boolean.valueOf(false));
    public static Setting skylight = new Setting("Skylight", "Prevents skylight updates from rendering", Boolean.valueOf(false));
    public static Setting hurtCamera = new Setting("HurtCamera", "Removes the hurt camera effect", Boolean.valueOf(true));
    public static Setting witherSkull = new Setting("WitherSkull", "Prevents flying wither skulls from rendering", Boolean.valueOf(true));
    public static Setting potion = new Setting("Potion", "Removes certain potion effects", Boolean.valueOf(false));
    public static Setting fov = new Setting("FOV", "Removes the FOV modifier effect", Boolean.valueOf(true));

    public NoRender() {
        super("NoRender", Category.VISUAL, "Prevents certain things from rendering");
        NoRender.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) NoRender.items.getValue()).booleanValue()) {
            Iterator iterator = (new ArrayList(NoRender.mc.world.loadedEntityList)).iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityItem) {
                    NoRender.mc.world.removeEntity(entity);
                }
            }
        }

        if (((Boolean) NoRender.potion.getValue()).booleanValue()) {
            if (NoRender.mc.player.isPotionActive(MobEffects.BLINDNESS)) {
                NoRender.mc.player.removePotionEffect(MobEffects.BLINDNESS);
            }

            if (NoRender.mc.player.isPotionActive(MobEffects.NAUSEA)) {
                NoRender.mc.player.removePotionEffect(MobEffects.NAUSEA);
            }
        }

    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (this.nullCheck() && ((Boolean) NoRender.overlays.getValue()).booleanValue()) {
            if (event.getOverlayType().equals(OverlayType.FIRE) && ((Boolean) NoRender.overlayFire.getValue()).booleanValue()) {
                event.setCanceled(true);
            }

            if (event.getOverlayType().equals(OverlayType.WATER) && ((Boolean) NoRender.overlayLiquid.getValue()).booleanValue()) {
                event.setCanceled(true);
            }

            if (event.getOverlayType().equals(OverlayType.BLOCK) && ((Boolean) NoRender.overlayBlock.getValue()).booleanValue()) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onRenderBossOverlay(BossOverlayEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.overlayBoss.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderEnchantmentTableBook(RenderEnchantmentTableBookEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.tileEntities.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderBeaconBeam(RenderBeaconBeamEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.tileEntities.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderSkylight(RenderSkylightEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.skylight.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderMap(RenderMapEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.maps.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onLayerArmor(LayerArmorEvent event) {
        if (this.nullCheck() && ((Boolean) NoRender.armor.getValue()).booleanValue()) {
            event.setCanceled(true);
            switch (event.getEntityEquipmentSlot()) {
            case HEAD:
                event.getModelBiped().bipedHead.showModel = false;
                event.getModelBiped().bipedHeadwear.showModel = false;
                break;

            case CHEST:
                event.getModelBiped().bipedBody.showModel = false;
                event.getModelBiped().bipedRightArm.showModel = false;
                event.getModelBiped().bipedLeftArm.showModel = false;
                break;

            case LEGS:
                event.getModelBiped().bipedBody.showModel = false;
                event.getModelBiped().bipedRightLeg.showModel = false;
                event.getModelBiped().bipedLeftLeg.showModel = false;
                break;

            case FEET:
                event.getModelBiped().bipedRightLeg.showModel = false;
                event.getModelBiped().bipedLeftLeg.showModel = false;

            case MAINHAND:
            case OFFHAND:
            }
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketParticles && ((SPacketParticles) event.getPacket()).getParticleCount() > 200) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onHurtCamera(HurtCameraEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.hurtCamera.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderWitherSkull(RenderWitherSkullEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.witherSkull.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onRenderFog(FogDensity event) {
        if (this.nullCheck() && ((Boolean) NoRender.fog.getValue()).booleanValue()) {
            if (!PlayerUtil.isInLiquid() && ((Boolean) NoRender.fogLiquid.getValue()).booleanValue()) {
                return;
            }

            event.setDensity((float) ((Double) NoRender.fogDensity.getValue()).doubleValue());
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onFOVModifier(ModifyFOVEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) NoRender.fov.getValue()).booleanValue());
    }
}
