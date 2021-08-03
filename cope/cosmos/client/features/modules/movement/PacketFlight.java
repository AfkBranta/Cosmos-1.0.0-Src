package cope.cosmos.client.features.modules.movement;

import cope.cosmos.client.events.MotionEvent;
import cope.cosmos.client.events.MotionUpdateEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ISPacketPlayerPosLook;
import cope.cosmos.util.player.MotionUtil;
import cope.cosmos.util.world.TeleportUtil;
import io.netty.util.internal.ConcurrentSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook.EnumFlags;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class PacketFlight extends Module {

    public static PacketFlight INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for PacketFlight", PacketFlight.Mode.FAST);
    public static Setting direction = new Setting("Direction", "Direction of the bounds packets", PacketFlight.Direction.DOWN);
    public static Setting factor = new Setting(get<invokedynamic>(), "Factor", "Speed factor", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 1);
    public static Setting subdivisions = new Setting(get<invokedynamic>(), "Subdivisions", "How many rotations packets to send", Double.valueOf(0.0D), Double.valueOf(4.0D), Double.valueOf(10.0D), 0);
    public static Setting antiKick = new Setting("AntiKick", "Prevents getting kicked by vanilla anti-cheat", Boolean.valueOf(true));
    public static Setting confirm = new Setting("Confirm", "Proactively confirms packets", Boolean.valueOf(true));
    public static Setting overshoot = new Setting("Overshoot", "Slightly overshoots the packet positions", Boolean.valueOf(false));
    public static Setting stabilize = new Setting("Stabilize", "Ignores server position and rotation requests", Boolean.valueOf(true));
    int lastTeleportId;
    ConcurrentSet safePackets = new ConcurrentSet();
    ConcurrentHashMap vectorMap = new ConcurrentHashMap();
    float clientYaw = Float.NaN;
    float clientPitch = Float.NaN;
    double clientX = Double.NaN;
    double clientY = Double.NaN;
    double clientZ = Double.NaN;
    float serverYaw = Float.NaN;
    float serverPitch = Float.NaN;
    double serverX = Double.NaN;
    double serverY = Double.NaN;
    double serverZ = Double.NaN;

    public PacketFlight() {
        super("PacketFlight", Category.MOVEMENT, "Fly with packet exploit.", get<invokedynamic>());
        PacketFlight.INSTANCE = this;
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (PacketFlight.mc.player == null) {
            this.getAnimation().setState(false);
            this.disable();
        }

        if (PacketFlight.mc.player != null && PacketFlight.mc.player.getHealth() <= 0.0F) {
            this.getAnimation().setState(false);
            this.disable();
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (this.nullCheck() && !((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.QUICK)) {
            PacketFlight.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            double[] motion = this.getMotion(this.isPlayerClipped() ? (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.FACTOR) ? ((Double) PacketFlight.factor.getValue()).doubleValue() : 1.0D) : 1.0D);

            PacketFlight.mc.player.motionX = motion[0];
            PacketFlight.mc.player.motionY = motion[1];
            PacketFlight.mc.player.motionZ = motion[2];
            PacketFlight.mc.player.setVelocity(motion[0], motion[1], motion[2]);
            PacketFlight.mc.player.noClip = true;
            this.processPackets(new double[] { motion[0], motion[1], motion[2]});
        }

    }

    @SubscribeEvent
    public void onMove(MotionEvent event) {
        if (this.nullCheck() && !((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.QUICK)) {
            event.setCanceled(true);
            double[] motion = this.getMotion(this.isPlayerClipped() ? (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.FACTOR) ? ((Double) PacketFlight.factor.getValue()).doubleValue() : 1.0D) : 1.0D);

            event.setX(motion[0]);
            event.setY(motion[1]);
            event.setZ(motion[2]);
            PacketFlight.mc.player.setVelocity(motion[0], motion[1], motion[2]);
            PacketFlight.mc.player.noClip = true;
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            event.setCanceled(!this.safePackets.contains(packet));
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (this.nullCheck()) {
            this.clientYaw = PacketFlight.mc.player.rotationYaw;
            this.clientPitch = PacketFlight.mc.player.rotationPitch;
            this.clientX = PacketFlight.mc.player.posX;
            this.clientY = PacketFlight.mc.player.getEntityBoundingBox().minY;
            this.clientZ = PacketFlight.mc.player.posZ;
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                Vec3d packetVector = (Vec3d) this.vectorMap.remove(Integer.valueOf(packet.getTeleportId()));

                if (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.FAST) && packetVector != null && packetVector.x == packet.getX() && packetVector.y == packet.getY() && packetVector.z == packet.getZ()) {
                    event.setCanceled(((Boolean) PacketFlight.stabilize.getValue()).booleanValue());
                    return;
                }

                this.serverYaw = packet.getYaw();
                this.serverPitch = packet.getPitch();
                this.serverX = packet.getX();
                this.serverY = packet.getY();
                this.serverZ = packet.getZ();
                if (packet.getFlags().contains(EnumFlags.X)) {
                    this.serverX += PacketFlight.mc.player.posX;
                } else {
                    PacketFlight.mc.player.motionX = 0.0D;
                }

                if (packet.getFlags().contains(EnumFlags.Y)) {
                    this.serverY += PacketFlight.mc.player.posY;
                } else {
                    PacketFlight.mc.player.motionY = 0.0D;
                }

                if (packet.getFlags().contains(EnumFlags.Z)) {
                    this.serverZ += PacketFlight.mc.player.posZ;
                } else {
                    PacketFlight.mc.player.motionZ = 0.0D;
                }

                if (packet.getFlags().contains(EnumFlags.X_ROT)) {
                    this.serverPitch += PacketFlight.mc.player.rotationPitch;
                }

                if (packet.getFlags().contains(EnumFlags.Y_ROT)) {
                    this.serverYaw += PacketFlight.mc.player.rotationYaw;
                }

                if (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.PATCH)) {
                    event.setCanceled(((Boolean) PacketFlight.stabilize.getValue()).booleanValue());
                    TeleportUtil.teleportPlayerKeepMotion(this.serverX, this.serverY, this.serverZ);
                    if (((Boolean) PacketFlight.confirm.getValue()).booleanValue()) {
                        PacketFlight.mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
                    }

                    PositionRotation serverPacket = new PositionRotation(this.serverX, this.serverY, this.serverZ, this.serverYaw, this.serverPitch, false);

                    this.safePackets.add(serverPacket);
                    PacketFlight.mc.player.connection.sendPacket(serverPacket);
                } else {
                    ((ISPacketPlayerPosLook) packet).setYaw(this.clientYaw);
                    ((ISPacketPlayerPosLook) packet).setPitch(this.clientPitch);
                }

                this.lastTeleportId = packet.getTeleportId();
            }
        }

    }

    @SubscribeEvent
    public void onPush(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(this.nullCheck() && event.getEntityPlayer().equals(PacketFlight.mc.player));
    }

    private void processPackets(double[] motion) {
        Vec3d increment = new Vec3d(motion[0], motion[1], motion[2]);
        Vec3d playerIncrement = PacketFlight.mc.player.getPositionVector().add(increment);
        Vec3d bounded = this.getBoundingVectors(increment, playerIncrement);
        PositionRotation legit = new PositionRotation(playerIncrement.x + (((Boolean) PacketFlight.overshoot.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-1.0D, 1.0D) : 0.0D), playerIncrement.y + (((Boolean) PacketFlight.overshoot.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-1.0D, 1.0D) : 0.0D), playerIncrement.z + (((Boolean) PacketFlight.overshoot.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-1.0D, 1.0D) : 0.0D), PacketFlight.mc.player.rotationYaw, PacketFlight.mc.player.rotationPitch, PacketFlight.mc.player.onGround);
        PositionRotation bounds = new PositionRotation(bounded.x, ((PacketFlight.Direction) PacketFlight.direction.getValue()).equals(PacketFlight.Direction.GROUND) ? 0.0D : bounded.y, bounded.z, PacketFlight.mc.player.rotationYaw, PacketFlight.mc.player.rotationPitch, !((PacketFlight.Direction) PacketFlight.direction.getValue()).equals(PacketFlight.Direction.GROUND) || PacketFlight.mc.player.onGround);

        this.safePackets.add(legit);
        this.safePackets.add(bounds);
        PacketFlight.mc.player.connection.sendPacket(legit);
        PacketFlight.mc.player.connection.sendPacket(bounds);
        if (!this.vectorMap.containsKey(Integer.valueOf(this.lastTeleportId)) && ((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.PATCH)) {
            if (((Boolean) PacketFlight.confirm.getValue()).booleanValue()) {
                PacketFlight.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.lastTeleportId++));
            }

            TeleportUtil.teleportPlayerKeepMotion(playerIncrement.x, playerIncrement.y, playerIncrement.z);
            this.vectorMap.put(Integer.valueOf(this.lastTeleportId), playerIncrement);
        }

    }

    private double[] getMotion(double factor) {
        double motionY = this.getMotionY();
        double speed;

        if (motionY != 0.0D) {
            speed = 0.026D;
        } else {
            speed = 0.04D;
        }

        double[] motion = MotionUtil.getMoveSpeed(motionY != 0.0D ? speed : speed * factor);

        if (!this.isPlayerClipped()) {
            if (motionY != 0.0D && motionY != -0.0325D) {
                motion[0] = 0.0D;
                motion[1] = 0.0D;
            } else if (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.PATCH)) {
                motion[0] *= 2.3425D;
                motion[1] *= 2.3425D;
            } else {
                motion[0] *= 3.59125D;
                motion[1] *= 3.59125D;
            }
        } else if (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.PATCH)) {
            motion[0] *= 0.8D;
            motion[1] *= 0.8D;
        } else if (((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.STRICT)) {
            motion[0] *= 0.75D;
            motion[1] *= 0.75D;
        }

        return new double[] { motion[0], motionY, motion[1]};
    }

    private double getMotionY() {
        double motionY = 0.0D;

        if (!this.isPlayerClipped()) {
            if (PacketFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
                motionY = 0.031D;
                if (PacketFlight.mc.player.ticksExisted % 18 == 0 && ((Boolean) PacketFlight.antiKick.getValue()).booleanValue()) {
                    motionY = -0.04D;
                }
            }

            if (PacketFlight.mc.gameSettings.keyBindSneak.isKeyDown() && !PacketFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
                motionY = -0.031D;
            }
        } else {
            if (PacketFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
                motionY = 0.017D;
            }

            if (PacketFlight.mc.gameSettings.keyBindSneak.isKeyDown() && !PacketFlight.mc.gameSettings.keyBindJump.isKeyDown()) {
                motionY = -0.017D;
            }
        }

        if (motionY == 0.0D && !this.isPlayerClipped() && PacketFlight.mc.player.ticksExisted % 4 == 0 && ((Boolean) PacketFlight.antiKick.getValue()).booleanValue()) {
            motionY = -0.0325D;
        }

        return motionY;
    }

    private Vec3d getBoundingVectors(Vec3d one, Vec3d two) {
        Vec3d newVector = one.add(two);

        switch ((PacketFlight.Direction) PacketFlight.direction.getValue()) {
        case UP:
            return newVector.add(0.0D, 80085.69D, 0.0D);

        case DOWN:
        default:
            return newVector.add(0.0D, -80085.69D, 0.0D);

        case RANDOM:
            return newVector.add(0.0D, ThreadLocalRandom.current().nextDouble(-80085.69D, 80085.69D), 0.0D);

        case GROUND:
        case NONE:
            return newVector.add(0.0D, 0.0D, 0.0D);
        }
    }

    private boolean isPlayerClipped() {
        return !PacketFlight.mc.world.getCollisionBoxes(PacketFlight.mc.player, PacketFlight.mc.player.getEntityBoundingBox().contract(0.125D, 0.15D, 0.125D)).isEmpty();
    }

    private static Boolean lambda$static$2() {
        return Boolean.valueOf(((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.PATCH));
    }

    private static Boolean lambda$static$1() {
        return Boolean.valueOf(((PacketFlight.Mode) PacketFlight.mode.getValue()).equals(PacketFlight.Mode.FACTOR));
    }

    private static String lambda$new$0() {
        return Setting.formatEnum((Enum) PacketFlight.mode.getValue());
    }

    public static enum Direction {

        UP, DOWN, GROUND, RANDOM, NONE;
    }

    public static enum Mode {

        FAST, FACTOR, STRICT, PATCH, QUICK;
    }
}
