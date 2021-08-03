package cope.cosmos.client.features.modules.movement;

import cope.cosmos.client.events.LivingUpdateEvent;
import cope.cosmos.client.events.MotionEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.MotionUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {

    public static Sprint INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for sprint", Sprint.Mode.DIRECTIONAL);
    public static Setting safe = new Setting("Safe", "Stops sprinting when you don\'t have the required hunger", Boolean.valueOf(false));
    public static Setting strict = new Setting("Strict", "Stops sprinting when sneaking and using items", Boolean.valueOf(false));

    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Sprints continuously");
        Sprint.INSTANCE = this;
    }

    public void onUpdate() {
        switch ((Sprint.Mode) Sprint.mode.getValue()) {
        case DIRECTIONAL:
            Sprint.mc.player.setSprinting(this.handleSprint() && MotionUtil.isMoving());
            break;

        case NORMAL:
            Sprint.mc.player.setSprinting(this.handleSprint() && MotionUtil.isMoving() && !Sprint.mc.player.collidedHorizontally && Sprint.mc.gameSettings.keyBindForward.isKeyDown());
        }

    }

    @SubscribeEvent
    public void onMotion(MotionEvent event) {
        event.setCanceled(this.nullCheck() && this.handleSprint() && MotionUtil.isMoving() && ((Sprint.Mode) Sprint.mode.getValue()).equals(Sprint.Mode.DIRECTIONAL));
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        event.setCanceled(this.nullCheck() && this.handleSprint() && MotionUtil.isMoving() && ((Sprint.Mode) Sprint.mode.getValue()).equals(Sprint.Mode.DIRECTIONAL));
    }

    public boolean handleSprint() {
        return Sprint.mc.player.getFoodStats().getFoodLevel() <= 6 && ((Boolean) Sprint.safe.getValue()).booleanValue() ? false : !Sprint.mc.player.isHandActive() && !Sprint.mc.player.isSneaking() || !((Boolean) Sprint.strict.getValue()).booleanValue();
    }

    public static enum Mode {

        DIRECTIONAL, NORMAL;
    }
}
