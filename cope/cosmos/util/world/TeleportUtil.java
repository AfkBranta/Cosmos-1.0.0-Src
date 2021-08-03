package cope.cosmos.util.world;

import cope.cosmos.util.Wrapper;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class TeleportUtil implements Wrapper {

    public static void teleportPlayer(double x, double y, double z) {
        TeleportUtil.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        TeleportUtil.mc.player.setPosition(x, y, z);
        TeleportUtil.mc.player.connection.sendPacket(new Position(x, y, z, true));
    }

    public static void teleportPlayerNoPacket(double x, double y, double z) {
        TeleportUtil.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        TeleportUtil.mc.player.setPosition(x, y, z);
    }

    public static void teleportPlayerKeepMotion(double x, double y, double z) {
        TeleportUtil.mc.player.setPosition(x, y, z);
    }
}
