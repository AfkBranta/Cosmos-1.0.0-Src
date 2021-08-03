package cope.cosmos.loader.asm.mixins.accessor;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ EntityPlayerSP.class})
public interface IEntityPlayerSP {

    @Accessor("serverSprintState")
    boolean getServerSprintState();

    @Accessor("serverSneakState")
    boolean getServerSneakState();

    @Accessor("positionUpdateTicks")
    int getPositionUpdateTicks();

    @Accessor("lastReportedPosX")
    double getLastReportedPosX();

    @Accessor("lastReportedPosY")
    double getLastReportedPosY();

    @Accessor("lastReportedPosZ")
    double getLastReportedPosZ();

    @Accessor("lastReportedYaw")
    float getLastReportedYaw();

    @Accessor("lastReportedPitch")
    float getLastReportedPitch();

    @Accessor("prevOnGround")
    boolean getPreviousOnGround();

    @Accessor("serverSprintState")
    void setServerSprintState(boolean flag);

    @Accessor("serverSneakState")
    void setServerSneakState(boolean flag);

    @Accessor("positionUpdateTicks")
    void setPositionUpdateTicks(int i);

    @Accessor("lastReportedPosX")
    void setLastReportedPosX(double d0);

    @Accessor("lastReportedPosY")
    void setLastReportedPosY(double d0);

    @Accessor("lastReportedPosZ")
    void setLastReportedPosZ(double d0);

    @Accessor("lastReportedYaw")
    void setLastReportedYaw(float f);

    @Accessor("lastReportedPitch")
    void setLastReportedPitch(float f);

    @Accessor("prevOnGround")
    void setPreviousOnGround(boolean flag);
}
