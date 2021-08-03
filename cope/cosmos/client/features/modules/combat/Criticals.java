package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.system.Timer;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {

    public static Criticals INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for criticals", Criticals.Mode.PACKET);
    public static Setting motion = new Setting(get<invokedynamic>(), "Motion", "Vertical motion", Double.valueOf(0.0D), Double.valueOf(0.4D), Double.valueOf(1.0D), 2);
    public static Setting delay = new Setting("Delay", "Delay between attacks to attempt criticals", Double.valueOf(0.0D), Double.valueOf(200.0D), Double.valueOf(2000.0D), 0);
    public static Setting delayThirtyTwoK = (new Setting("32K", "Delay between 32K attacks to attempt criticals", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(2000.0D), 0)).setParent(Criticals.delay);
    public static Setting reset = new Setting("Reset", "Resets the player position after attempting to critical", Boolean.valueOf(false));
    public static Setting teleport = new Setting("Teleport", "Teleports up slightly to sync positions", Boolean.valueOf(false));
    public static Setting particles = new Setting("Particles", "Show critical particles", Boolean.valueOf(true));
    public static Setting fallBack = new Setting("FallBack", "Resets player packets after attempting criticals", Criticals.FallBack.CONFIRM);
    public static Setting pause = new Setting("Pause", "When to pause", Boolean.valueOf(true));
    public static Setting pauseLiquid = (new Setting("Liquid", "Pause in Liquid", Boolean.valueOf(true))).setParent(Criticals.pause);
    public static Setting pauseAir = (new Setting("Air", "Pause when falling or flying", Boolean.valueOf(true))).setParent(Criticals.pause);
    public static Setting pauseCrystal = (new Setting("Crystal", "Pause if attacking crystal", Boolean.valueOf(true))).setParent(Criticals.pause);
    public static Setting pauseThirtyTwoK = (new Setting("32K", "Pause if using 32K", Boolean.valueOf(true))).setParent(Criticals.pause);
    Timer criticalTimer = new Timer();

    public Criticals() {
        super("Criticals", Category.COMBAT, "Ensures all hits are criticals");
        Criticals.INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (this.nullCheck() && event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getAction().equals(Action.ATTACK) && ((CPacketUseEntity) event.getPacket()).getEntityFromWorld(Criticals.mc.world) != null) {
            if (Aura.INSTANCE.isEnabled() && ((Aura.Timing) Aura.timing.getValue()).equals(Aura.Timing.SEQUENTIAL)) {
                return;
            }

            this.handleCriticals((Entity) Objects.requireNonNull(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(Criticals.mc.world)));
            this.handleFallback((Entity) Objects.requireNonNull(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(Criticals.mc.world)));
        }

    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        if (this.nullCheck()) {
            event.setDamageModifier(1.5F);
        }

    }

    public void handleCriticals(Entity entity) {
        if (((Boolean) Criticals.pause.getValue()).booleanValue()) {
            if (PlayerUtil.isInLiquid() && ((Boolean) Criticals.pauseLiquid.getValue()).booleanValue()) {
                return;
            }

            if (!((Criticals.Mode) Criticals.mode.getValue()).equals(Criticals.Mode.MOTION) && (Criticals.mc.player.fallDistance > 5.0F || !Criticals.mc.player.onGround) && ((Boolean) Criticals.pauseAir.getValue()).booleanValue()) {
                return;
            }

            if (Objects.requireNonNull(entity) instanceof EntityEnderCrystal && ((Boolean) Criticals.pauseCrystal.getValue()).booleanValue()) {
                return;
            }

            if (InventoryUtil.isHolding32k() && ((Boolean) Criticals.pauseThirtyTwoK.getValue()).booleanValue()) {
                return;
            }
        }

        Criticals.mc.player.fallDistance = 0.2F;
        if (this.criticalTimer.passed((long) (InventoryUtil.isHolding32k() ? (Double) Criticals.delayThirtyTwoK.getValue() : (Double) Criticals.delay.getValue()).doubleValue(), Timer.Format.SYSTEM)) {
            switch ((Criticals.Mode) Criticals.mode.getValue()) {
            case PACKET:
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.05D, Criticals.mc.player.posZ, false));
                this.resetPacket(0.05D);
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                this.resetPacket(0.0D);
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.03D, Criticals.mc.player.posZ, false));
                this.resetPacket(0.03D);
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                this.resetPacket(0.0D);
                break;

            case STRICT:
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.062602401692772D, Criticals.mc.player.posZ, false));
                this.resetPacket(0.062602401692772D);
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0726023996066094D, Criticals.mc.player.posZ, false));
                this.resetPacket(0.0726023996066094D);
                Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                this.resetPacket(0.0D);
                break;

            case MOTION:
                Criticals.mc.player.motionY = ((Double) Criticals.motion.getValue()).doubleValue();
            }

            this.criticalTimer.reset();
        }

    }

    public void handleFallback(Entity entity) {
        switch ((Criticals.Mode) Criticals.mode.getValue()) {
        case PACKET:
        case STRICT:
            switch ((Criticals.FallBack) Criticals.fallBack.getValue()) {
            case CONFIRM:
                Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
                break;

            case RANDOM:
                Criticals.mc.player.connection.sendPacket(new CPacketPlayer(ThreadLocalRandom.current().nextBoolean()));
            }

            if (((Boolean) Criticals.particles.getValue()).booleanValue()) {
                Criticals.mc.player.onCriticalHit(entity);
            }
            break;

        case MOTION:
            if (((Boolean) Criticals.particles.getValue()).booleanValue()) {
                Criticals.mc.player.onCriticalHit(entity);
            }
        }

    }

    public void resetPacket(double sent) {
        if (((Boolean) Criticals.reset.getValue()).booleanValue()) {
            Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        }

        if (((Boolean) Criticals.teleport.getValue()).booleanValue()) {
            Criticals.mc.player.setPosition(Criticals.mc.player.posX, Criticals.mc.player.posY + sent, Criticals.mc.player.posZ);
        }

    }

    private static Boolean lambda$static$0() {
        return Boolean.valueOf(((Criticals.Mode) Criticals.mode.getValue()).equals(Criticals.Mode.MOTION));
    }

    public static enum FallBack {

        CONFIRM, RANDOM, NONE;
    }

    public static enum Mode {

        PACKET, STRICT, MOTION;
    }
}
