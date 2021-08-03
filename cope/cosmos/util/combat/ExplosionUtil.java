package cope.cosmos.util.combat;

import cope.cosmos.util.Wrapper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;

public class ExplosionUtil implements Wrapper {

    public static float getDamageFromExplosion(double explosionX, double explosionY, double explosionZ, Entity target, boolean ignoreTerrain, boolean prediction) {
        try {
            return (float) ((double) getExplosionDamage(target, new Vec3d(explosionX, explosionY, explosionZ), 6.0F, ignoreTerrain, prediction) / 2.333D);
        } catch (Exception exception) {
            return 0.0F;
        }
    }

    public static float getExplosionDamage(Entity targetEntity, Vec3d explosionPosition, float explosionPower, boolean ignoreTerrain, boolean prediction) {
        Vec3d entityPosition = prediction && targetEntity instanceof EntityPlayer ? new Vec3d(targetEntity.posX + targetEntity.motionX, targetEntity.posY + targetEntity.motionY, targetEntity.posZ + targetEntity.motionZ) : new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ);

        if (targetEntity.isImmuneToExplosions()) {
            return 0.0F;
        } else {
            explosionPower *= 2.0F;
            double distanceToSize = entityPosition.distanceTo(explosionPosition) / (double) explosionPower;
            double blockDensity = 0.0D;
            AxisAlignedBB entityBox = targetEntity.getEntityBoundingBox().offset(targetEntity.getPositionVector().subtract(entityPosition));
            Vec3d boxDelta = new Vec3d(1.0D / ((entityBox.maxX - entityBox.minX) * 2.0D + 1.0D), 1.0D / ((entityBox.maxY - entityBox.minY) * 2.0D + 1.0D), 1.0D / ((entityBox.maxZ - entityBox.minZ) * 2.0D + 1.0D));
            double xOff = (1.0D - Math.floor(1.0D / boxDelta.x) * boxDelta.x) / 2.0D;
            double zOff = (1.0D - Math.floor(1.0D / boxDelta.z) * boxDelta.z) / 2.0D;

            if (boxDelta.x >= 0.0D && boxDelta.y >= 0.0D && boxDelta.z >= 0.0D) {
                int densityAdjust = 0;
                int total = 0;

                for (double damage = 0.0D; damage <= 1.0D; damage += boxDelta.x) {
                    for (double y = 0.0D; y <= 1.0D; y += boxDelta.y) {
                        for (double z = 0.0D; z <= 1.0D; z += boxDelta.z) {
                            Vec3d startPos = new Vec3d(xOff + entityBox.minX + (entityBox.maxX - entityBox.minX) * damage, entityBox.minY + (entityBox.maxY - entityBox.minY) * y, zOff + entityBox.minZ + (entityBox.maxZ - entityBox.minZ) * z);

                            if (!rayTraceSolidCheck(startPos, explosionPosition, ignoreTerrain)) {
                                ++densityAdjust;
                            }

                            ++total;
                        }
                    }
                }

                blockDensity = (double) densityAdjust / (double) total;
            }

            double d0 = (1.0D - distanceToSize) * blockDensity;
            float f = (float) ((int) ((d0 * d0 + d0) / 2.0D * 7.0D * (double) explosionPower + 1.0D));

            if (targetEntity instanceof EntityLivingBase) {
                f = getBlastReduction((EntityLivingBase) targetEntity, getDamageFromDifficulty(f, ExplosionUtil.mc.world.getDifficulty()), new Explosion(ExplosionUtil.mc.world, (Entity) null, explosionPosition.x, explosionPosition.y, explosionPosition.z, explosionPower / 2.0F, false, true));
            }

            return f;
        }
    }

    public static boolean rayTraceSolidCheck(Vec3d start, Vec3d end, boolean ignoreTerrain) {
        if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z) && !Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
            int currX = MathHelper.floor(start.x);
            int currY = MathHelper.floor(start.y);
            int currZ = MathHelper.floor(start.z);
            int endX = MathHelper.floor(end.x);
            int endY = MathHelper.floor(end.y);
            int endZ = MathHelper.floor(end.z);
            BlockPos blockPos = new BlockPos(currX, currY, currZ);
            IBlockState blockState = ExplosionUtil.mc.world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (blockState.getCollisionBoundingBox(ExplosionUtil.mc.world, blockPos) != Block.NULL_AABB && block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !ignoreTerrain)) {
                RayTraceResult seDeltaX = blockState.collisionRayTrace(ExplosionUtil.mc.world, blockPos, start, end);

                if (seDeltaX != null) {
                    return true;
                }
            }

            double d0 = end.x - start.x;
            double seDeltaY = end.y - start.y;
            double seDeltaZ = end.z - start.z;
            int steps = 200;

            while (steps-- >= 0) {
                if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
                    return false;
                }

                if (currX == endX && currY == endY && currZ == endZ) {
                    return false;
                }

                boolean unboundedX = true;
                boolean unboundedY = true;
                boolean unboundedZ = true;
                double stepX = 999.0D;
                double stepY = 999.0D;
                double stepZ = 999.0D;
                double deltaX = 999.0D;
                double deltaY = 999.0D;
                double deltaZ = 999.0D;

                if (endX > currX) {
                    stepX = (double) (currX + 1);
                } else if (endX < currX) {
                    stepX = (double) currX;
                } else {
                    unboundedX = false;
                }

                if (endY > currY) {
                    stepY = (double) (currY + 1);
                } else if (endY < currY) {
                    stepY = (double) currY;
                } else {
                    unboundedY = false;
                }

                if (endZ > currZ) {
                    stepZ = (double) (currZ + 1);
                } else if (endZ < currZ) {
                    stepZ = (double) currZ;
                } else {
                    unboundedZ = false;
                }

                if (unboundedX) {
                    deltaX = (stepX - start.x) / d0;
                }

                if (unboundedY) {
                    deltaY = (stepY - start.y) / seDeltaY;
                }

                if (unboundedZ) {
                    deltaZ = (stepZ - start.z) / seDeltaZ;
                }

                if (deltaX == 0.0D) {
                    deltaX = -1.0E-4D;
                }

                if (deltaY == 0.0D) {
                    deltaY = -1.0E-4D;
                }

                if (deltaZ == 0.0D) {
                    deltaZ = -1.0E-4D;
                }

                EnumFacing facing;

                if (deltaX < deltaY && deltaX < deltaZ) {
                    facing = endX > currX ? EnumFacing.WEST : EnumFacing.EAST;
                    start = new Vec3d(stepX, start.y + seDeltaY * deltaX, start.z + seDeltaZ * deltaX);
                } else if (deltaY < deltaZ) {
                    facing = endY > currY ? EnumFacing.DOWN : EnumFacing.UP;
                    start = new Vec3d(start.x + d0 * deltaY, stepY, start.z + seDeltaZ * deltaY);
                } else {
                    facing = endZ > currZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    start = new Vec3d(start.x + d0 * deltaZ, start.y + seDeltaY * deltaZ, stepZ);
                }

                currX = MathHelper.floor(start.x) - (facing == EnumFacing.EAST ? 1 : 0);
                currY = MathHelper.floor(start.y) - (facing == EnumFacing.UP ? 1 : 0);
                currZ = MathHelper.floor(start.z) - (facing == EnumFacing.SOUTH ? 1 : 0);
                blockPos = new BlockPos(currX, currY, currZ);
                blockState = ExplosionUtil.mc.world.getBlockState(blockPos);
                block = blockState.getBlock();
                if (block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !ignoreTerrain)) {
                    RayTraceResult collisionInterCheck = blockState.collisionRayTrace(ExplosionUtil.mc.world, blockPos, start, end);

                    if (collisionInterCheck != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        float enchantmentModifierDamage = 0.0F;

        try {
            enchantmentModifierDamage = (float) EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion));
        } catch (Exception exception) {
            ;
        }

        enchantmentModifierDamage = MathHelper.clamp(enchantmentModifierDamage, 0.0F, 20.0F);
        damage *= 1.0F - enchantmentModifierDamage / 25.0F;
        PotionEffect resistanceEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);

        if (entity.isPotionActive(MobEffects.RESISTANCE) && resistanceEffect != null) {
            damage = damage * (float) (25 - (resistanceEffect.getAmplifier() + 1) * 5) / 25.0F;
        }

        damage = Math.max(damage, 0.0F);
        return damage;
    }

    public static List getBlocks() {
        ArrayList list = new ArrayList();

        list.add(Blocks.OBSIDIAN);
        list.add(Blocks.BEDROCK);
        list.add(Blocks.COMMAND_BLOCK);
        list.add(Blocks.BARRIER);
        list.add(Blocks.ENCHANTING_TABLE);
        list.add(Blocks.END_PORTAL_FRAME);
        list.add(Blocks.BEACON);
        list.add(Blocks.ANVIL);
        list.add(Blocks.ENDER_CHEST);
        return list;
    }

    public static float getDamageFromDifficulty(float damage, EnumDifficulty difficulty) {
        switch (difficulty) {
        case PEACEFUL:
            return 0.0F;

        case EASY:
            return damage * 0.5F;

        case NORMAL:
            return damage;

        case HARD:
        default:
            return damage * 1.5F;
        }
    }
}
