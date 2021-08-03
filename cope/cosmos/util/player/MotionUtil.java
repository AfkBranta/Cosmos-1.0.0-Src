package cope.cosmos.util.player;

import cope.cosmos.util.Wrapper;
import net.minecraft.entity.Entity;

public class MotionUtil implements Wrapper {

    public static void setMoveSpeed(double speed, float stepHeight) {
        Object currentMover = MotionUtil.mc.player.isRiding() ? MotionUtil.mc.player.getRidingEntity() : MotionUtil.mc.player;

        if (currentMover != null) {
            float forward = MotionUtil.mc.player.movementInput.moveForward;
            float strafe = MotionUtil.mc.player.movementInput.moveStrafe;
            float yaw = MotionUtil.mc.player.rotationYaw;

            if (!isMoving()) {
                ((Entity) currentMover).motionX = 0.0D;
                ((Entity) currentMover).motionZ = 0.0D;
            } else if (forward != 0.0F) {
                if (strafe >= 1.0F) {
                    yaw += (float) (forward > 0.0F ? -45 : 45);
                    strafe = 0.0F;
                } else if (strafe <= -1.0F) {
                    yaw += (float) (forward > 0.0F ? 45 : -45);
                    strafe = 0.0F;
                }

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }

            double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
            double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));

            ((Entity) currentMover).motionX = (double) forward * speed * cos + (double) strafe * speed * sin;
            ((Entity) currentMover).motionZ = (double) forward * speed * sin - (double) strafe * speed * cos;
            ((Entity) currentMover).stepHeight = stepHeight;
            if (!isMoving()) {
                ((Entity) currentMover).motionX = 0.0D;
                ((Entity) currentMover).motionZ = 0.0D;
            }
        }

    }

    public static double[] getMoveSpeed(double speed) {
        float forward = MotionUtil.mc.player.movementInput.moveForward;
        float strafe = MotionUtil.mc.player.movementInput.moveStrafe;
        float yaw = MotionUtil.mc.player.rotationYaw;

        if (!isMoving()) {
            return new double[] { 0.0D, 0.0D};
        } else {
            if (forward != 0.0F) {
                if (strafe >= 1.0F) {
                    yaw += (float) (forward > 0.0F ? -45 : 45);
                    strafe = 0.0F;
                } else if (strafe <= -1.0F) {
                    yaw += (float) (forward > 0.0F ? 45 : -45);
                    strafe = 0.0F;
                }

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }

            double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
            double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
            double motionX = (double) forward * speed * cos + (double) strafe * speed * sin;
            double motionZ = (double) forward * speed * sin - (double) strafe * speed * cos;

            return new double[] { motionX, motionZ};
        }
    }

    public static void stopMotion(double fall) {
        Object currentMover = MotionUtil.mc.player.isRiding() ? MotionUtil.mc.player.getRidingEntity() : MotionUtil.mc.player;

        if (currentMover != null) {
            ((Entity) currentMover).setVelocity(0.0D, fall, 0.0D);
        }

    }

    public static boolean isMoving() {
        return MotionUtil.mc.player.moveForward != 0.0F || MotionUtil.mc.player.moveStrafing != 0.0F;
    }
}
