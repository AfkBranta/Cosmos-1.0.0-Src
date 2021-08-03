package cope.cosmos.client.manager.managers;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.loader.asm.mixins.accessor.IMinecraft;
import cope.cosmos.loader.asm.mixins.accessor.ITimer;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.system.MathUtil;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TickManager extends Manager implements Wrapper {

    public long prevTime = -1L;
    public float[] TPS = new float[20];
    public int currentTick;

    public TickManager() {
        super("TickManager", "Keeps track of the server ticks", 16);
        int i = 0;

        for (int len = this.TPS.length; i < len; ++i) {
            this.TPS[i] = 0.0F;
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void initialize(Manager manager) {
        new TickManager();
    }

    public float getTPS(TickManager.TPS tps) {
        switch (tps) {
        case CURRENT:
            return TickManager.mc.isSingleplayer() ? 20.0F : (float) MathUtil.roundDouble((double) MathHelper.clamp(this.TPS[0], 0.0F, 20.0F), 2);

        case AVERAGE:
            int tickCount = 0;
            float tickRate = 0.0F;
            float[] afloat = this.TPS;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float tick = afloat[j];

                if (tick > 0.0F) {
                    tickRate += tick;
                    ++tickCount;
                }
            }

            return TickManager.mc.isSingleplayer() ? 20.0F : (float) MathUtil.roundDouble((double) MathHelper.clamp(tickRate / (float) tickCount, 0.0F, 20.0F), 2);

        default:
            return 0.0F;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (this.prevTime != -1L) {
                this.TPS[this.currentTick % this.TPS.length] = MathHelper.clamp(20.0F / ((float) (System.currentTimeMillis() - this.prevTime) / 1000.0F), 0.0F, 20.0F);
                ++this.currentTick;
            }

            this.prevTime = System.currentTimeMillis();
        }

    }

    public void setClientTicks(double ticks) {
        ((ITimer) ((IMinecraft) TickManager.mc).getTimer()).setTickLength((float) (50.0D / ticks));
    }

    public static enum TPS {

        CURRENT, AVERAGE, NONE;
    }
}
