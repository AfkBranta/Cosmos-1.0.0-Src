package cope.cosmos.client.features.modules.movement;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.world.HoleUtil;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class ReverseStep extends Module {

    public static ReverseStep INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for pulling down", ReverseStep.Mode.MOTION);
    public static Setting height = new Setting("Height", "Required height to be pulled down", Double.valueOf(0.0D), Double.valueOf(2.0D), Double.valueOf(5.0D), 1);
    public static Setting speed = new Setting("Speed", "Pull down speed", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(10.0D), 2);
    public static Setting shift = new Setting("Shift", "Sneaks the player before pulling down", Boolean.valueOf(false));
    public static Setting strict = new Setting("Strict", "Uses a slower speed to bypass strict servers", Boolean.valueOf(false));
    public static Setting hole = new Setting("OnlyHole", "Only pulls you down into holes", Boolean.valueOf(false));

    public ReverseStep() {
        super("ReverseStep", Category.MOVEMENT, "Allows you to fall faster");
        ReverseStep.INSTANCE = this;
    }

    public void onUpdate() {
        if (!PlayerUtil.isInLiquid() && !ReverseStep.mc.gameSettings.keyBindJump.isKeyDown() && !ReverseStep.mc.player.isOnLadder()) {
            if (!((Boolean) ReverseStep.hole.getValue()).booleanValue() || HoleUtil.isAboveHole(((Double) ReverseStep.height.getValue()).doubleValue())) {
                double y;

                switch ((ReverseStep.Mode) ReverseStep.mode.getValue()) {
                case MOTION:
                    if (ReverseStep.mc.player.onGround) {
                        if (((Boolean) ReverseStep.shift.getValue()).booleanValue()) {
                            ReverseStep.mc.player.connection.sendPacket(new CPacketEntityAction(ReverseStep.mc.player, Action.START_SNEAKING));
                        }

                        for (y = 0.0D; y < ((Double) ReverseStep.height.getValue()).doubleValue() + 0.5D; y += 0.01D) {
                            if (!ReverseStep.mc.world.getCollisionBoxes(ReverseStep.mc.player, ReverseStep.mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
                                ReverseStep.mc.player.motionY = ((Boolean) ReverseStep.strict.getValue()).booleanValue() ? -0.22D : -((Double) ReverseStep.speed.getValue()).doubleValue();
                                break;
                            }
                        }

                        if (((Boolean) ReverseStep.shift.getValue()).booleanValue()) {
                            ReverseStep.mc.player.connection.sendPacket(new CPacketEntityAction(ReverseStep.mc.player, Action.STOP_SNEAKING));
                        }
                    }
                    break;

                case SHIFT:
                    if (ReverseStep.mc.player.onGround) {
                        for (y = 0.0D; y < ((Double) ReverseStep.height.getValue()).doubleValue() + 0.5D; y += 0.01D) {
                            if (!ReverseStep.mc.world.getCollisionBoxes(ReverseStep.mc.player, ReverseStep.mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
                                ReverseStep.mc.player.connection.sendPacket(new CPacketPlayer(ReverseStep.mc.player.onGround));
                                ReverseStep.mc.player.motionY *= 1.75D;
                                ReverseStep.mc.player.connection.sendPacket(new Position(ReverseStep.mc.player.posX, ReverseStep.mc.player.posY, ReverseStep.mc.player.posZ, false));
                                break;
                            }
                        }
                    }
                    break;

                case TIMER:
                    Cosmos.INSTANCE.getTickManager().setClientTicks(1.0D);
                    if (ReverseStep.mc.player.onGround) {
                        for (y = 0.0D; y < ((Double) ReverseStep.height.getValue()).doubleValue() + 0.5D; y += 0.01D) {
                            if (!ReverseStep.mc.world.getCollisionBoxes(ReverseStep.mc.player, ReverseStep.mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
                                Cosmos.INSTANCE.getTickManager().setClientTicks(((Double) ReverseStep.speed.getValue()).doubleValue() * 2.0D);
                                break;
                            }
                        }
                    }
                }

            }
        }
    }

    public static enum Mode {

        MOTION, SHIFT, TIMER;
    }
}
