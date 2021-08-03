package cope.cosmos.client.features.modules.movement;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.TravelEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.MotionUtil;
import cope.cosmos.util.player.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlight extends Module {

    public static ElytraFlight INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for ElytraFlight", ElytraFlight.Elytra.CONTROL);
    public static Setting yaw = new Setting(get<invokedynamic>(), "Yaw", "Maximum allowed yaw", Double.valueOf(0.0D), Double.valueOf(30.0D), Double.valueOf(90.0D), 1);
    public static Setting pitch = new Setting(get<invokedynamic>(), "Pitch", "Maximum allowed pitch", Double.valueOf(0.0D), Double.valueOf(30.0D), Double.valueOf(90.0D), 1);
    public static Setting glide = new Setting("Glide", "Speed when gliding", Double.valueOf(0.0D), Double.valueOf(2.5D), Double.valueOf(5.0D), 2);
    public static Setting ascend = new Setting("Ascend", "Speed when ascending", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 2);
    public static Setting descend = new Setting("Descend", "Speed when descending", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 2);
    public static Setting fall = new Setting("Fall", "Speed when stationary", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(0.1D), 3);
    public static Setting firework = new Setting("Firework", "Mode to switch to fireworks if necessary", InventoryUtil.Switch.NONE);
    public static Setting lockRotation = new Setting("LockRotation", "Locks rotation and flies in a straight path", Boolean.valueOf(false));
    public static Setting takeOff = new Setting("TakeOff", "Easier takeoff", Boolean.valueOf(false));
    public static Setting takeOffTimer = (new Setting("Timer", "Timer ticks when taking off", Double.valueOf(0.0D), Double.valueOf(0.2D), Double.valueOf(1.0D), 2)).setParent(ElytraFlight.takeOff);
    public static Setting pause = new Setting("Pause", "Pause elytra flight when", Boolean.valueOf(true));
    public static Setting pauseLiquid = (new Setting("Liquid", "When in liquid", Boolean.valueOf(true))).setParent(ElytraFlight.pause);
    public static Setting pauseCollision = (new Setting("Collision", "When colliding", Boolean.valueOf(false))).setParent(ElytraFlight.pause);

    public ElytraFlight() {
        super("ElytraFlight", Category.MOVEMENT, "Allows you to fly faster on an elytra");
        ElytraFlight.INSTANCE = this;
    }

    @SubscribeEvent
    public void onTravel(TravelEvent event) {
        try {
            if (this.nullCheck() && this.handlePause() && ElytraFlight.mc.player.isElytraFlying()) {
                this.elytraFlight(event);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void onEnable() {
        super.onEnable();
        if (!ElytraFlight.mc.player.isElytraFlying() && ((Boolean) ElytraFlight.takeOff.getValue()).booleanValue()) {
            Cosmos.INSTANCE.getTickManager().setClientTicks(((Double) ElytraFlight.takeOffTimer.getValue()).doubleValue());
            if (ElytraFlight.mc.player.onGround) {
                ElytraFlight.mc.player.motionY = 0.4D;
            } else {
                ElytraFlight.mc.player.connection.sendPacket(new CPacketEntityAction(ElytraFlight.mc.player, Action.START_FALL_FLYING));
            }
        }

    }

    public void elytraFlight(TravelEvent event) {
        event.setCanceled(true);
        Cosmos.INSTANCE.getTickManager().setClientTicks(1.0D);
        if (((Boolean) ElytraFlight.lockRotation.getValue()).booleanValue()) {
            ElytraFlight.mc.player.rotationYaw = (float) MathHelper.clamp((double) ElytraFlight.mc.player.rotationYaw, -((Double) ElytraFlight.yaw.getValue()).doubleValue(), ((Double) ElytraFlight.yaw.getValue()).doubleValue());
            ElytraFlight.mc.player.rotationPitch = (float) MathHelper.clamp((double) ElytraFlight.mc.player.rotationPitch, -((Double) ElytraFlight.pitch.getValue()).doubleValue(), ((Double) ElytraFlight.pitch.getValue()).doubleValue());
        }

        MotionUtil.stopMotion(-((Double) ElytraFlight.fall.getValue()).doubleValue());
        MotionUtil.setMoveSpeed(((Double) ElytraFlight.glide.getValue()).doubleValue(), 0.6F);
        switch ((ElytraFlight.Elytra) ElytraFlight.mode.getValue()) {
        case CONTROL:
            this.handleControl();
            break;

        case STRICT:
            this.handleStrict();

        case PACKET:
        }

        PlayerUtil.lockLimbs();
    }

    public void handleControl() {
        if (ElytraFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
            ElytraFlight.mc.player.motionY = ((Double) ElytraFlight.ascend.getValue()).doubleValue();
        } else if (ElytraFlight.mc.gameSettings.keyBindSneak.isKeyDown()) {
            ElytraFlight.mc.player.motionY = -((Double) ElytraFlight.descend.getValue()).doubleValue();
        }

    }

    public void handleStrict() {
        if (ElytraFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
            ElytraFlight.mc.player.rotationPitch = (float) (-((Double) ElytraFlight.pitch.getValue()).doubleValue());
            ElytraFlight.mc.player.motionY = ((Double) ElytraFlight.ascend.getValue()).doubleValue();
        } else if (ElytraFlight.mc.gameSettings.keyBindSneak.isKeyDown()) {
            ElytraFlight.mc.player.rotationPitch = (float) ((Double) ElytraFlight.pitch.getValue()).doubleValue();
            ElytraFlight.mc.player.motionY = -((Double) ElytraFlight.descend.getValue()).doubleValue();
        }

    }

    public boolean handlePause() {
        if (((Boolean) ElytraFlight.pause.getValue()).booleanValue()) {
            if (PlayerUtil.isInLiquid() && ((Boolean) ElytraFlight.pauseLiquid.getValue()).booleanValue()) {
                return true;
            }

            if (PlayerUtil.isCollided() && ((Boolean) ElytraFlight.pauseCollision.getValue()).booleanValue()) {
                return true;
            }
        }

        return true;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (this.nullCheck() && event.getPacket() instanceof SPacketPlayerPosLook && !((InventoryUtil.Switch) ElytraFlight.firework.getValue()).equals(InventoryUtil.Switch.NONE) && ElytraFlight.mc.player.isElytraFlying()) {
            InventoryUtil.switchToSlot(Items.FIREWORKS, (InventoryUtil.Switch) ElytraFlight.firework.getValue());
            ElytraFlight.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }

    }

    private static Boolean lambda$static$1() {
        return Boolean.valueOf(((ElytraFlight.Elytra) ElytraFlight.mode.getValue()).equals(ElytraFlight.Elytra.STRICT));
    }

    private static Boolean lambda$static$0() {
        return Boolean.valueOf(((ElytraFlight.Elytra) ElytraFlight.mode.getValue()).equals(ElytraFlight.Elytra.STRICT));
    }

    public static enum Elytra {

        CONTROL, STRICT, PACKET;
    }
}
