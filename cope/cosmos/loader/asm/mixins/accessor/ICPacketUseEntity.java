package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ CPacketUseEntity.class})
public interface ICPacketUseEntity {

    @Accessor("entityID")
    void setID(int i);

    @Accessor("action")
    void setAction(Action action);
}
