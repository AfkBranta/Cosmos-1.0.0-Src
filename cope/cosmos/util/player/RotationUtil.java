package cope.cosmos.util.player;

import cope.cosmos.client.events.MotionUpdateEvent;
import cope.cosmos.loader.asm.mixins.accessor.IEntityPlayerSP;
import cope.cosmos.util.Wrapper;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;

public class RotationUtil implements Wrapper {

    public static void updateRotationPackets(MotionUpdateEvent event) {
        if (RotationUtil.mc.player.isSprinting() != ((IEntityPlayerSP) RotationUtil.mc.player).getServerSprintState()) {
            if (RotationUtil.mc.player.isSprinting()) {
                RotationUtil.mc.player.connection.sendPacket(new CPacketEntityAction(RotationUtil.mc.player, Action.START_SPRINTING));
            } else {
                RotationUtil.mc.player.connection.sendPacket(new CPacketEntityAction(RotationUtil.mc.player, Action.STOP_SPRINTING));
            }

            ((IEntityPlayerSP) RotationUtil.mc.player).setServerSprintState(RotationUtil.mc.player.isSprinting());
        }

        if (RotationUtil.mc.player.isSneaking() != ((IEntityPlayerSP) RotationUtil.mc.player).getServerSneakState()) {
            if (RotationUtil.mc.player.isSneaking()) {
                RotationUtil.mc.player.connection.sendPacket(new CPacketEntityAction(RotationUtil.mc.player, Action.START_SNEAKING));
            } else {
                RotationUtil.mc.player.connection.sendPacket(new CPacketEntityAction(RotationUtil.mc.player, Action.STOP_SNEAKING));
            }

            ((IEntityPlayerSP) RotationUtil.mc.player).setServerSneakState(RotationUtil.mc.player.isSneaking());
        }

        double updatedPosX = RotationUtil.mc.player.posX - ((IEntityPlayerSP) RotationUtil.mc.player).getLastReportedPosX();
        double updatedPosY = RotationUtil.mc.player.getEntityBoundingBox().minY - ((IEntityPlayerSP) RotationUtil.mc.player).getLastReportedPosY();
        double updatedPosZ = RotationUtil.mc.player.posZ - ((IEntityPlayerSP) RotationUtil.mc.player).getLastReportedPosZ();
        double updatedRotationYaw = (double) (event.getYaw() - ((IEntityPlayerSP) RotationUtil.mc.player).getLastReportedYaw());
        double updatedRotationPitch = (double) (event.getPitch() - ((IEntityPlayerSP) RotationUtil.mc.player).getLastReportedPitch());
        int positionUpdateTicks = ((IEntityPlayerSP) RotationUtil.mc.player).getPositionUpdateTicks();

        ((IEntityPlayerSP) RotationUtil.mc.player).setPositionUpdateTicks(positionUpdateTicks++);
        boolean positionUpdate = updatedPosX * updatedPosX + updatedPosY * updatedPosY + updatedPosZ * updatedPosZ > 9.0E-4D || ((IEntityPlayerSP) RotationUtil.mc.player).getPositionUpdateTicks() >= 20;
        boolean rotationUpdate = updatedRotationYaw != 0.0D || updatedRotationPitch != 0.0D;

        if (RotationUtil.mc.player.isRiding()) {
            RotationUtil.mc.player.connection.sendPacket(new PositionRotation(RotationUtil.mc.player.motionX, -999.0D, RotationUtil.mc.player.motionZ, event.getYaw(), event.getPitch(), RotationUtil.mc.player.onGround));
            positionUpdate = false;
        } else if (positionUpdate && rotationUpdate) {
            RotationUtil.mc.player.connection.sendPacket(new PositionRotation(RotationUtil.mc.player.posX, RotationUtil.mc.player.getEntityBoundingBox().minY, RotationUtil.mc.player.posZ, event.getYaw(), event.getPitch(), RotationUtil.mc.player.onGround));
        } else if (positionUpdate) {
            RotationUtil.mc.player.connection.sendPacket(new Position(RotationUtil.mc.player.posX, RotationUtil.mc.player.getEntityBoundingBox().minY, RotationUtil.mc.player.posZ, RotationUtil.mc.player.onGround));
        } else if (rotationUpdate) {
            RotationUtil.mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPacketPlayer.Rotation(event.getYaw(), event.getPitch(), RotationUtil.mc.player.onGround));
        } else if (((IEntityPlayerSP) RotationUtil.mc.player).getPreviousOnGround() != RotationUtil.mc.player.onGround) {
            RotationUtil.mc.player.connection.sendPacket(new CPacketPlayer(RotationUtil.mc.player.onGround));
        }

        if (positionUpdate) {
            ((IEntityPlayerSP) RotationUtil.mc.player).setLastReportedPosX(RotationUtil.mc.player.posX);
            ((IEntityPlayerSP) RotationUtil.mc.player).setLastReportedPosY(RotationUtil.mc.player.getEntityBoundingBox().minY);
            ((IEntityPlayerSP) RotationUtil.mc.player).setLastReportedPosZ(RotationUtil.mc.player.posZ);
            ((IEntityPlayerSP) RotationUtil.mc.player).setPositionUpdateTicks(0);
        }

        if (rotationUpdate) {
            ((IEntityPlayerSP) RotationUtil.mc.player).setLastReportedYaw(event.getYaw());
            ((IEntityPlayerSP) RotationUtil.mc.player).setLastReportedPitch(event.getPitch());
        }

        ((IEntityPlayerSP) RotationUtil.mc.player).setPreviousOnGround(RotationUtil.mc.player.onGround);
    }
}
