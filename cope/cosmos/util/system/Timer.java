package cope.cosmos.util.system;

import cope.cosmos.util.Wrapper;

public class Timer implements Wrapper {

    public long time = -1L;

    public long getMS(long time) {
        return time / 1000000L;
    }

    public boolean passed(long time, Timer.Format format) {
        switch (format) {
        case SYSTEM:
        default:
            return this.getMS(System.nanoTime() - this.time) >= time;

        case TICKS:
            return Timer.mc.player.ticksExisted % (int) time == 0;
        }
    }

    public boolean reach(long time, Timer.Format format) {
        switch (format) {
        case SYSTEM:
        default:
            return this.getMS(System.nanoTime() - this.time) <= time;

        case TICKS:
            return Timer.mc.player.ticksExisted % (int) time != 0;
        }
    }

    public boolean sleep(long time) {
        if (System.nanoTime() / 1000000L - time >= time) {
            this.reset();
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        this.time = System.nanoTime();
    }

    public static enum Format {

        SYSTEM, TICKS;
    }
}
