package cope.cosmos.loader.asm.mixins;

import cope.cosmos.client.events.EntityHitboxSizeEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Entity.class})
public class MixinEntity {

    @Inject(
        method = { "getCollisionBorderSize"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void getCollisionBorderSize(CallbackInfoReturnable info) {
        EntityHitboxSizeEvent entityHitboxSizeEvent = new EntityHitboxSizeEvent();

        MinecraftForge.EVENT_BUS.post(entityHitboxSizeEvent);
        if (entityHitboxSizeEvent.isCanceled()) {
            info.cancel();
            info.setReturnValue(Float.valueOf(entityHitboxSizeEvent.getHitboxSize()));
        }

    }
}
