package cope.cosmos.util.world;

import cope.cosmos.util.Wrapper;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HoleUtil implements Wrapper {

    public static boolean isInHole(double posX, double posY, double posZ) {
        return isObsidianHole(new BlockPos(posX, (double) Math.round(posY), posZ)) || isBedRockHole(new BlockPos(posX, (double) Math.round(posY), posZ));
    }

    public static boolean isInHole(Entity entity) {
        return isObsidianHole(new BlockPos(entity.posX, (double) Math.round(entity.posY), entity.posZ)) || isBedRockHole(new BlockPos(entity.posX, (double) Math.round(entity.posY), entity.posZ));
    }

    public static boolean isPartOfHole(BlockPos blockPos) {
        ArrayList entities = new ArrayList();

        entities.addAll(HoleUtil.mc.world.getEntitiesWithinAABBExcludingEntity(HoleUtil.mc.player, new AxisAlignedBB(blockPos.add(1, 0, 0))));
        entities.addAll(HoleUtil.mc.world.getEntitiesWithinAABBExcludingEntity(HoleUtil.mc.player, new AxisAlignedBB(blockPos.add(-1, 0, 0))));
        entities.addAll(HoleUtil.mc.world.getEntitiesWithinAABBExcludingEntity(HoleUtil.mc.player, new AxisAlignedBB(blockPos.add(0, 0, 1))));
        entities.addAll(HoleUtil.mc.world.getEntitiesWithinAABBExcludingEntity(HoleUtil.mc.player, new AxisAlignedBB(blockPos.add(0, 0, -1))));
        return entities.stream().anyMatch((entity) -> {
            return entity instanceof EntityPlayer;
        });
    }

    public static boolean isAboveHole(double height) {
        Vec3d belowVector = InterpolationUtil.interpolateEntityTime(HoleUtil.mc.player, HoleUtil.mc.getRenderPartialTicks());

        return isObsidianHole(new BlockPos(belowVector.x, belowVector.y - height, belowVector.z)) || isBedRockHole(new BlockPos(belowVector.x, belowVector.y - height, belowVector.z));
    }

    public static boolean isDoubleBedrockHoleX(BlockPos blockPos) {
        if (HoleUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && HoleUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR)) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))) {
            BlockPos[] ablockpos = new BlockPos[] { blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)};
            int i = ablockpos.length;

            for (int j = 0; j < i; ++j) {
                BlockPos connectedPos = ablockpos[j];

                if (BlockUtil.getBlockResistance(connectedPos) == BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.UNBREAKABLE) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isDoubleBedrockHoleZ(BlockPos blockPos) {
        if (HoleUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && HoleUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR)) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))) {
            BlockPos[] ablockpos = new BlockPos[] { blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)};
            int i = ablockpos.length;

            for (int j = 0; j < i; ++j) {
                BlockPos connectedPos = ablockpos[j];

                if (BlockUtil.getBlockResistance(connectedPos) == BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.UNBREAKABLE) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isDoubleObsidianHoleX(BlockPos blockPos) {
        if (isDoubleBedrockHoleX(blockPos)) {
            return false;
        } else if (HoleUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && HoleUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR)) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))) {
            BlockPos[] ablockpos = new BlockPos[] { blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)};
            int i = ablockpos.length;

            for (int j = 0; j < i; ++j) {
                BlockPos connectedPos = ablockpos[j];

                if (BlockUtil.getBlockResistance(connectedPos) == BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.UNBREAKABLE) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isDoubleObsidianHoleZ(BlockPos blockPos) {
        if (isDoubleBedrockHoleZ(blockPos)) {
            return false;
        } else if (HoleUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && HoleUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR)) && (HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) || HoleUtil.mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))) {
            BlockPos[] ablockpos = new BlockPos[] { blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)};
            int i = ablockpos.length;

            for (int j = 0; j < i; ++j) {
                BlockPos connectedPos = ablockpos[j];

                if (BlockUtil.getBlockResistance(connectedPos) == BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(connectedPos) != BlockUtil.BlockResistance.UNBREAKABLE) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isObsidianHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockUtil.BlockResistance.BLANK && !isBedRockHole(blockPos) && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockUtil.BlockResistance.BLANK && (BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockUtil.BlockResistance.RESISTANT || BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockUtil.BlockResistance.UNBREAKABLE) && (BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockUtil.BlockResistance.RESISTANT || BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE) && (BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockUtil.BlockResistance.RESISTANT || BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE) && (BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockUtil.BlockResistance.RESISTANT || BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockUtil.BlockResistance.UNBREAKABLE) && BlockUtil.getBlockResistance(blockPos.add(0.5D, 0.5D, 0.5D)) == BlockUtil.BlockResistance.BLANK && (BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockUtil.BlockResistance.RESISTANT || BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockUtil.BlockResistance.UNBREAKABLE);
    }

    public static boolean isBedRockHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0.5D, 0.5D, 0.5D)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockUtil.BlockResistance.UNBREAKABLE;
    }

    public static boolean isVoidHole(BlockPos blockPos) {
        return HoleUtil.mc.player.dimension == -1 ? (blockPos.getY() == 0 || blockPos.getY() == 127) && (Objects.equals(BlockUtil.getBlockResistance(blockPos), BlockUtil.BlockResistance.BLANK) || Objects.equals(BlockUtil.getBlockResistance(blockPos), BlockUtil.BlockResistance.RESISTANT)) : blockPos.getY() == 0 && (Objects.equals(BlockUtil.getBlockResistance(blockPos), BlockUtil.BlockResistance.BLANK) || Objects.equals(BlockUtil.getBlockResistance(blockPos), BlockUtil.BlockResistance.RESISTANT));
    }
}
