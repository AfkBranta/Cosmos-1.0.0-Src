package cope.cosmos.util.world;

import cope.cosmos.client.features.modules.combat.AutoCrystal;
import cope.cosmos.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtil implements Wrapper {

    public static boolean raytraceBlock(BlockPos blockPos, AutoCrystal.Raytrace raytrace) {
        return RaytraceUtil.mc.world.rayTraceBlocks(new Vec3d(RaytraceUtil.mc.player.posX, RaytraceUtil.mc.player.posY + (double) RaytraceUtil.mc.player.getEyeHeight(), RaytraceUtil.mc.player.posZ), new Vec3d((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + raytrace.getOffset(), (double) blockPos.getZ() + 0.5D), false, true, false) != null;
    }

    public static boolean raytraceEntity(Entity entity, double offset) {
        return RaytraceUtil.mc.world.rayTraceBlocks(new Vec3d(RaytraceUtil.mc.player.posX, RaytraceUtil.mc.player.posY + (double) RaytraceUtil.mc.player.getEyeHeight(), RaytraceUtil.mc.player.posZ), new Vec3d(entity.posX, entity.posY + offset, entity.posZ), false, true, false) == null;
    }
}
