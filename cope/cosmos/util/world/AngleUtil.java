package cope.cosmos.util.world;

import cope.cosmos.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AngleUtil implements Wrapper {

    public static float[] calculateAngles(Entity entity) {
        return calculateAngle(InterpolationUtil.interpolateEntityTime(AngleUtil.mc.player, AngleUtil.mc.getRenderPartialTicks()), InterpolationUtil.interpolateEntityTime(entity, AngleUtil.mc.getRenderPartialTicks()));
    }

    public static float[] calculateAngles(BlockPos blockPos) {
        return calculateAngle(InterpolationUtil.interpolateEntityTime(AngleUtil.mc.player, AngleUtil.mc.getRenderPartialTicks()), new Vec3d(blockPos));
    }

    public static float[] calculateCenter(Entity entity) {
        return calculateAngle(InterpolationUtil.interpolateEntityTime(AngleUtil.mc.player, AngleUtil.mc.getRenderPartialTicks()), InterpolationUtil.interpolateEntityTime(entity, AngleUtil.mc.getRenderPartialTicks()).add(new Vec3d((double) (entity.width / 2.0F), (double) (entity.getEyeHeight() / 2.0F), (double) (entity.width / 2.0F))));
    }

    public static float[] calculateCenter(BlockPos blockPos) {
        return calculateAngle(InterpolationUtil.interpolateEntityTime(AngleUtil.mc.player, AngleUtil.mc.getRenderPartialTicks()), (new Vec3d(blockPos)).add(new Vec3d(0.5D, 0.0D, 0.5D)));
    }

    public static float[] calculateAngle(Vec3d from, Vec3d to) {
        return new float[] { (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(to.z - from.z, to.x - from.x)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2((to.y - from.y) * -1.0D, (double) MathHelper.sqrt(Math.pow(to.x - from.x, 2.0D) + Math.pow(to.z - from.z, 2.0D)))))};
    }

    public static float calculateAngleDifference(float serverValue, float currentValue, double divisions, int steps) {
        return (float) ((double) serverValue - (double) currentValue / (divisions * (double) steps));
    }

    public static float calculateAngleDifference(float direction, float rotationYaw) {
        float phi = Math.abs(rotationYaw - direction) % 360.0F;

        return phi > 180.0F ? 360.0F - phi : phi;
    }
}
