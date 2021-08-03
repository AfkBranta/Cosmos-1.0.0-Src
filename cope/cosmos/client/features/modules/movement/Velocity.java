package cope.cosmos.client.features.modules.movement;

import cope.cosmos.client.events.EntityCollisionEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.WaterCollisionEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ISPacketEntityVelocity;
import cope.cosmos.loader.asm.mixins.accessor.ISPacketExplosion;
import java.util.function.Supplier;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {

    public static Velocity INSTANCE;
    public static Setting horizontal = new Setting("Horizontal", "Horizontal velocity modifier", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), 2);
    public static Setting vertical = new Setting("Vertical", "Vertical velocity modifier", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), 2);
    public static Setting noPush = new Setting("NoPush", "Prevents being pushed", Boolean.valueOf(true));
    public static Setting entities = (new Setting("Entities", "Prevents being pushed by entities", Boolean.valueOf(true))).setParent(Velocity.noPush);
    public static Setting blocks = (new Setting("Blocks", "Prevents being pushed out of blocks", Boolean.valueOf(true))).setParent(Velocity.noPush);
    public static Setting liquid = (new Setting("Liquid", "Prevents being pushed by liquids", Boolean.valueOf(true))).setParent(Velocity.noPush);
    float collisionReduction;

    public Velocity() {
        super("Velocity", Category.MOVEMENT, "Take no knockback.", () -> {
            return "H" + Velocity.horizontal.getValue() + "%, V" + Velocity.vertical.getValue() + "%";
        });
        Velocity.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) Velocity.noPush.getValue()).booleanValue()) {
            Velocity.mc.player.entityCollisionReduction = 1.0F;
        }

    }

    public void onEnable() {
        super.onEnable();
        this.collisionReduction = Velocity.mc.player.entityCollisionReduction;
    }

    public void onDisable() {
        super.onDisable();
        Velocity.mc.player.entityCollisionReduction = this.collisionReduction;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (this.nullCheck()) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                if (((Double) Velocity.horizontal.getValue()).doubleValue() == 0.0D && ((Double) Velocity.vertical.getValue()).doubleValue() == 0.0D) {
                    event.setCanceled(true);
                    return;
                }

                SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();

                if (packet.getEntityID() == Velocity.mc.player.getEntityId()) {
                    ((ISPacketEntityVelocity) packet).setMotionX(((ISPacketEntityVelocity) packet).getMotionX() / 100 * ((Double) Velocity.horizontal.getValue()).intValue());
                    ((ISPacketEntityVelocity) packet).setMotionY(((ISPacketEntityVelocity) packet).getMotionY() / 100 * ((Double) Velocity.vertical.getValue()).intValue());
                    ((ISPacketEntityVelocity) packet).setMotionZ(((ISPacketEntityVelocity) packet).getMotionZ() / 100 * ((Double) Velocity.horizontal.getValue()).intValue());
                }
            }

            if (event.getPacket() instanceof SPacketExplosion) {
                if (((Double) Velocity.horizontal.getValue()).doubleValue() == 0.0D && ((Double) Velocity.vertical.getValue()).doubleValue() == 0.0D) {
                    event.setCanceled(true);
                    return;
                }

                SPacketExplosion packet1 = (SPacketExplosion) event.getPacket();

                ((ISPacketExplosion) packet1).setMotionX((float) ((double) (((ISPacketExplosion) packet1).getMotionX() / 100.0F) * ((Double) Velocity.horizontal.getValue()).doubleValue()));
                ((ISPacketExplosion) packet1).setMotionY((float) ((double) (((ISPacketExplosion) packet1).getMotionY() / 100.0F) * ((Double) Velocity.vertical.getValue()).doubleValue()));
                ((ISPacketExplosion) packet1).setMotionZ((float) ((double) (((ISPacketExplosion) packet1).getMotionZ() / 100.0F) * ((Double) Velocity.horizontal.getValue()).doubleValue()));
            }
        }

    }

    @SubscribeEvent
    public void onPush(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) Velocity.noPush.getValue()).booleanValue() && ((Boolean) Velocity.blocks.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onKnockback(LivingKnockBackEvent event) {
        event.setCanceled(this.nullCheck() && ((Double) Velocity.horizontal.getValue()).doubleValue() == 0.0D && ((Double) Velocity.vertical.getValue()).doubleValue() == 0.0D);
    }

    @SubscribeEvent
    public void onEntityCollision(EntityCollisionEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) Velocity.noPush.getValue()).booleanValue() && ((Boolean) Velocity.entities.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onWaterCollision(WaterCollisionEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) Velocity.noPush.getValue()).booleanValue() && ((Boolean) Velocity.liquid.getValue()).booleanValue());
    }
}
