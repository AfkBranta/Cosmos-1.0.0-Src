package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ PlayerControllerMP.class})
public interface IPlayerControllerMP {

    @Accessor("curBlockDamageMP")
    void setCurrentBlockDamage(float f);

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int i);

    @Invoker("syncCurrentPlayItem")
    void syncCurrentPlayItem();
}
