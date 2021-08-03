package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ Entity.class})
public interface IEntity {

    @Accessor("inPortal")
    boolean getInPortal();

    @Accessor("inPortal")
    void setInPortal(boolean flag);

    @Invoker("setSize")
    void setSize(float f, float f1);
}
