package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketEntityVelocity.class})
public interface ISPacketEntityVelocity {

    @Accessor("motionX")
    int getMotionX();

    @Accessor("motionY")
    int getMotionY();

    @Accessor("motionZ")
    int getMotionZ();

    @Accessor("motionX")
    void setMotionX(int i);

    @Accessor("motionY")
    void setMotionY(int i);

    @Accessor("motionZ")
    void setMotionZ(int i);
}
