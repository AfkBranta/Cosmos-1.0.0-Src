package cope.cosmos.util.player;

import cope.cosmos.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Mouse;

public class PlayerUtil implements Wrapper {

    public static double getHealth() {
        return (double) (PlayerUtil.mc.player.getHealth() + PlayerUtil.mc.player.getAbsorptionAmount());
    }

    public static BlockPos getPosition() {
        return new BlockPos(PlayerUtil.mc.player.posX + 0.5D, PlayerUtil.mc.player.posY, PlayerUtil.mc.player.posZ + 0.5D);
    }

    public static void attackEntity(Entity entity, boolean packet, PlayerUtil.Hand hand, double d0) {
        if (Math.random() <= d0 / 100.0D) {
            if (packet) {
                PlayerUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
            } else {
                PlayerUtil.mc.playerController.attackEntity(PlayerUtil.mc.player, entity);
            }
        }

        swingArm(hand);
        PlayerUtil.mc.player.resetCooldown();
    }

    public static void swingArm(PlayerUtil.Hand hand) {
        switch (hand) {
        case MAINHAND:
            PlayerUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
            break;

        case OFFHAND:
            PlayerUtil.mc.player.swingArm(EnumHand.OFF_HAND);
            break;

        case PACKET:
            PlayerUtil.mc.player.connection.sendPacket(new CPacketAnimation(PlayerUtil.mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));

        case NONE:
        }

    }

    public static void lockLimbs() {
        PlayerUtil.mc.player.prevLimbSwingAmount = 0.0F;
        PlayerUtil.mc.player.limbSwingAmount = 0.0F;
        PlayerUtil.mc.player.limbSwing = 0.0F;
    }

    public static boolean isEating() {
        return PlayerUtil.mc.player.getHeldItemMainhand().getItemUseAction().equals(EnumAction.EAT) || PlayerUtil.mc.player.getHeldItemMainhand().getItemUseAction().equals(EnumAction.DRINK);
    }

    public static boolean isMending() {
        return InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && Mouse.isButtonDown(1);
    }

    public static boolean isMining() {
        return InventoryUtil.isHolding(Items.DIAMOND_PICKAXE) && PlayerUtil.mc.playerController.getIsHittingBlock();
    }

    public static boolean isInLiquid() {
        return PlayerUtil.mc.player.isInLava() || PlayerUtil.mc.player.isInWater();
    }

    public static boolean isCollided() {
        return PlayerUtil.mc.player.collidedHorizontally || PlayerUtil.mc.player.collidedVertically;
    }

    public static enum Hand {

        MAINHAND, OFFHAND, PACKET, NONE;
    }
}
