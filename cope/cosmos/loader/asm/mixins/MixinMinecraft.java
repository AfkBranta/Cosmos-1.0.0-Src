package cope.cosmos.loader.asm.mixins;

import cope.cosmos.util.Wrapper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Minecraft.class})
public class MixinMinecraft implements Wrapper {

}
