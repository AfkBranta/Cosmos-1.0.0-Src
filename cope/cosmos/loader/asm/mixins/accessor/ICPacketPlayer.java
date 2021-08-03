package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ CPacketPlayer.class})
public interface ICPacketPlayer {

    @Accessor("yaw")
    void setYaw(float f);

    @Accessor("pitch")
    void setPitch(float f);
}
