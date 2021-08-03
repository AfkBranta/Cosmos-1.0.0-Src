package cope.cosmos.util.world;

import cope.cosmos.util.Wrapper;
import cope.cosmos.util.system.MathUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BlockUtil implements Wrapper {

    public static void placeBlock(BlockPos blockPos, boolean packet, boolean antiGlitch) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing enumFacing = aenumfacing[j];

            if (getBlockResistance(blockPos.offset(enumFacing)) != BlockUtil.BlockResistance.BLANK) {
                Iterator iterator = BlockUtil.mc.world.loadedEntityList.iterator();

                Entity entity;

                do {
                    if (!iterator.hasNext()) {
                        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, Action.START_SNEAKING));
                        if (packet) {
                            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos.offset(enumFacing), enumFacing.getOpposite(), EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                        } else {
                            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, blockPos.offset(enumFacing), enumFacing.getOpposite(), new Vec3d(blockPos), EnumHand.MAIN_HAND);
                        }

                        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, Action.STOP_SNEAKING));
                        if (antiGlitch) {
                            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos.offset(enumFacing), enumFacing.getOpposite()));
                        }

                        return;
                    }

                    entity = (Entity) iterator.next();
                } while (!(new AxisAlignedBB(blockPos)).intersects(entity.getEntityBoundingBox()));

                return;
            }
        }

    }

    public static Iterator getNearbyBlocks(EntityPlayer player, double blockRange, boolean motion) {
        ArrayList nearbyBlocks = new ArrayList();
        int range = (int) MathUtil.roundDouble(blockRange, 0);

        if (motion) {
            player.getPosition().add(new Vec3i(player.motionX, player.motionY, player.motionZ));
        }

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    nearbyBlocks.add(player.getPosition().add(x, y, z));
                }
            }
        }

        return nearbyBlocks.stream().filter(test<invokedynamic>(blockRange)).sorted(Comparator.comparing(apply<invokedynamic>())).iterator();
    }

    public static List getSurroundingBlocks(EntityPlayer player, double blockRange, boolean motion) {
        ArrayList nearbyBlocks = new ArrayList();
        int range = (int) MathUtil.roundDouble(blockRange, 0);

        if (motion) {
            player.getPosition().add(new Vec3i(player.motionX, player.motionY, player.motionZ));
        }

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    nearbyBlocks.add(player.getPosition().add(x, y, z));
                }
            }
        }

        return (List) nearbyBlocks.stream().filter(test<invokedynamic>(blockRange)).sorted(Comparator.comparing(apply<invokedynamic>())).collect(Collectors.toList());
    }

    public static double getNearestBlockBelow() {
        for (double y = BlockUtil.mc.player.posY; y > 0.0D; y -= 0.001D) {
            if (!(BlockUtil.mc.world.getBlockState(new BlockPos(BlockUtil.mc.player.posX, y, BlockUtil.mc.player.posZ)).getBlock() instanceof BlockSlab) && BlockUtil.mc.world.getBlockState(new BlockPos(BlockUtil.mc.player.posX, y, BlockUtil.mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(BlockUtil.mc.world, new BlockPos(0, 0, 0)) != null) {
                return y;
            }
        }

        return -1.0D;
    }

    public static BlockUtil.BlockResistance getBlockResistance(BlockPos block) {
        return BlockUtil.mc.world.isAirBlock(block) ? BlockUtil.BlockResistance.BLANK : (BlockUtil.mc.world.getBlockState(block).getBlock().getBlockHardness(BlockUtil.mc.world.getBlockState(block), BlockUtil.mc.world, block) != -1.0F && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST) ? BlockUtil.BlockResistance.BREAKABLE : (!BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST) ? (!BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.BARRIER) ? null : BlockUtil.BlockResistance.UNBREAKABLE) : BlockUtil.BlockResistance.RESISTANT));
    }

    private static Double lambda$getSurroundingBlocks$3(BlockPos blockPos) {
        return Double.valueOf(BlockUtil.mc.player.getDistanceSq(blockPos));
    }

    private static boolean lambda$getSurroundingBlocks$2(double blockRange, BlockPos blockPos) {
        return BlockUtil.mc.player.getDistance((double) blockPos.getX() + 0.5D, (double) (blockPos.getY() + 1), (double) blockPos.getZ() + 0.5D) <= blockRange;
    }

    private static Double lambda$getNearbyBlocks$1(BlockPos blockPos) {
        return Double.valueOf(BlockUtil.mc.player.getDistanceSq(blockPos));
    }

    private static boolean lambda$getNearbyBlocks$0(double blockRange, BlockPos blockPos) {
        return BlockUtil.mc.player.getDistance((double) blockPos.getX() + 0.5D, (double) (blockPos.getY() + 1), (double) blockPos.getZ() + 0.5D) <= blockRange;
    }

    public static enum BlockResistance {

        BLANK, BREAKABLE, RESISTANT, UNBREAKABLE;
    }
}
