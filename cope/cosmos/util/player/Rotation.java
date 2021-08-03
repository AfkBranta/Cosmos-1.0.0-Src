package cope.cosmos.util.player;

import cope.cosmos.client.Cosmos;
import cope.cosmos.util.Wrapper;

public class Rotation implements Wrapper {

    private float yaw;
    private float pitch;
    private final Rotation.Rotate rotate;

    public Rotation(float yaw, float pitch, Rotation.Rotate rotate) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = rotate;
    }

    public void updateModelRotations() {
        if (this.nullCheck()) {
            switch (this.rotate) {
            case PACKET:
                Rotation.mc.player.renderYawOffset = this.yaw;
                Rotation.mc.player.rotationYawHead = this.yaw;
                Cosmos.INSTANCE.getRotationManager().setHeadPitch(this.pitch);
                break;

            case CLIENT:
                Rotation.mc.player.rotationYaw = this.yaw;
                Rotation.mc.player.rotationPitch = this.pitch;

            case NONE:
            }
        }

    }

    public void restoreRotations() {
        if (this.nullCheck()) {
            this.yaw = Rotation.mc.player.rotationYaw;
            this.pitch = Rotation.mc.player.rotationPitch;
        }

    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Rotation.Rotate getRotation() {
        return this.rotate;
    }

    public static enum Rotate {

        PACKET, CLIENT, NONE;
    }
}
