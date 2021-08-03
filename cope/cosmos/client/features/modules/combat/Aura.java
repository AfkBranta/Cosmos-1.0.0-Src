package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.TickManager;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketPlayer;
import cope.cosmos.util.combat.TargetUtil;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.player.Rotation;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.AngleUtil;
import cope.cosmos.util.world.InterpolationUtil;
import cope.cosmos.util.world.RaytraceUtil;
import cope.cosmos.util.world.TeleportUtil;
import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aura extends Module {

    public static Aura INSTANCE;
    public static Setting iterations = new Setting("Iterations", "Attacks per iteration", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 0);
    public static Setting variation = new Setting("Variation", "Probability of your hits doing damage", Double.valueOf(0.0D), Double.valueOf(100.0D), Double.valueOf(100.0D), 0);
    public static Setting range = new Setting("Range", "Range to attack entities", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(7.0D), 1);
    public static Setting timing = new Setting("Timing", "Mode for timing attacks", Aura.Timing.COOLDOWN);
    public static Setting delayMode = (new Setting("Mode", "Mode for timing units", Aura.Delay.SWING)).setParent(Aura.timing);
    public static Setting delayFactor = (new Setting(get<invokedynamic>(), "Factor", "Vanilla attack factor", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(1.0D), 2)).setParent(Aura.timing);
    public static Setting delay = (new Setting(get<invokedynamic>(), "Delay", "Attack Delay in ms", Double.valueOf(0.0D), Double.valueOf(1000.0D), Double.valueOf(2000.0D), 0)).setParent(Aura.timing);
    public static Setting delayTicks = (new Setting(get<invokedynamic>(), "Ticks", "Attack Delay in ticks", Double.valueOf(0.0D), Double.valueOf(15.0D), Double.valueOf(20.0D), 0)).setParent(Aura.timing);
    public static Setting delayTPS = (new Setting(get<invokedynamic>(), "TPS", "Sync attack timing to server ticks", TickManager.TPS.AVERAGE)).setParent(Aura.timing);
    public static Setting delaySwitch = (new Setting("Switch", "Time to delay attacks after switching items", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(500.0D), 0)).setParent(Aura.timing);
    public static Setting timer = new Setting("Timer", "Client-Side timer", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(2.0D), 2);
    public static Setting fov = new Setting("FOV", "Field of vision for the process to function", Double.valueOf(1.0D), Double.valueOf(180.0D), Double.valueOf(180.0D), 0);
    public static Setting weapon = new Setting("Weapon", "Weapon to use for attacking", Aura.Weapon.SWORD);
    public static Setting weaponOnly = (new Setting("OnlyWeapon", "Only attack if holding weapon", Boolean.valueOf(true))).setParent(Aura.weapon);
    public static Setting weaponThirtyTwoK = (new Setting("32K", "Only attack if holding 32k", Boolean.valueOf(false))).setParent(Aura.weapon);
    public static Setting weaponBlock = (new Setting("Block", "Automatically blocks if you\'re holding a shield", Boolean.valueOf(false))).setParent(Aura.weapon);
    public static Setting rotate = new Setting("Rotation", "Mode for attack rotations", Rotation.Rotate.NONE);
    public static Setting rotateStep = (new Setting("Step", "Number of divisions when sending rotation packets", Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(10.0F), 0)).setParent(Aura.rotate);
    public static Setting rotateCenter = (new Setting("Center", "Center rotations on target", Boolean.valueOf(false))).setParent(Aura.rotate);
    public static Setting rotateRandom = (new Setting("Random", "Randomize rotations to simulate real rotations", Boolean.valueOf(false))).setParent(Aura.rotate);
    public static Setting swing = new Setting("Swing", "Hand to swing", PlayerUtil.Hand.MAINHAND);
    public static Setting raytrace = new Setting("Raytrace", "Verify if target is visible", Boolean.valueOf(false));
    public static Setting packet = new Setting("Packet", "Attack with packets", Boolean.valueOf(true));
    public static Setting teleport = new Setting("Teleport", "Vanilla teleport to target", Boolean.valueOf(false));
    public static Setting stopSprint = new Setting("StopSprint", "Stops sprinting before attacking", Boolean.valueOf(false));
    public static Setting pause = new Setting("Pause", "When to pause", Boolean.valueOf(true));
    public static Setting pauseHealth = (new Setting("Health", "Pause when below this health", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(36.0D), 0)).setParent(Aura.pause);
    public static Setting pauseEating = (new Setting("Eating", "Pause when eating", Boolean.valueOf(false))).setParent(Aura.pause);
    public static Setting pauseMining = (new Setting("Mining", "Pause when mining", Boolean.valueOf(true))).setParent(Aura.pause);
    public static Setting pauseMending = (new Setting("Mending", "Pause when mending", Boolean.valueOf(false))).setParent(Aura.pause);
    public static Setting autoSwitch = new Setting("Switch", "Mode for switching to weapon", InventoryUtil.Switch.NORMAL);
    public static Setting target = new Setting("Target", "Priority for searching target", TargetUtil.Target.CLOSEST);
    public static Setting targetPlayers = (new Setting("Players", "Target players", Boolean.valueOf(true))).setParent(Aura.target);
    public static Setting targetPassives = (new Setting("Passives", "Target passives", Boolean.valueOf(false))).setParent(Aura.target);
    public static Setting targetNeutrals = (new Setting("Neutrals", "Target neutrals", Boolean.valueOf(false))).setParent(Aura.target);
    public static Setting targetHostiles = (new Setting("Hostiles", "Target hostiles", Boolean.valueOf(false))).setParent(Aura.target);
    public static Setting render = new Setting("Render", "Render a visual over the target", Boolean.valueOf(true));
    public static Setting color = (new Setting("Color", "Color of the visual", new Color(144, 0, 255, 45))).setParent(Aura.render);
    public static Entity auraTarget = null;
    public static Timer auraTimer = new Timer();
    public static Rotation auraRotation = new Rotation(Float.NaN, Float.NaN, (Rotation.Rotate) Aura.rotate.getValue());

    public Aura() {
        super("Aura", Category.COMBAT, "Attacks nearby entities", get<invokedynamic>());
        Aura.INSTANCE = this;
    }

    public void onUpdate() {
        Aura.auraTarget = TargetUtil.getTargetEntity(((Double) Aura.range.getValue()).doubleValue(), (TargetUtil.Target) Aura.target.getValue(), ((Boolean) Aura.targetPlayers.getValue()).booleanValue(), ((Boolean) Aura.targetPassives.getValue()).booleanValue(), ((Boolean) Aura.targetNeutrals.getValue()).booleanValue(), ((Boolean) Aura.targetHostiles.getValue()).booleanValue());
        if (Aura.auraTarget != null && this.handlePause()) {
            this.killAura();
        }

    }

    public void onRender3d() {
        if (Aura.auraTarget != null && ((Boolean) Aura.render.getValue()).booleanValue()) {
            RenderUtil.drawCircle((new RenderBuilder()).setup().line(1.5F).depth(true).blend().texture(), InterpolationUtil.getInterpolatedPos(Aura.auraTarget, 1.0F), (double) Aura.auraTarget.width, (double) Aura.auraTarget.height * 0.5D * (Math.sin((double) Aura.mc.player.ticksExisted * 3.5D * 0.017453292519943295D) + 1.0D), (Color) Aura.color.getValue());
        }

    }

    public void killAura() {
        InventoryUtil.switchToSlot(((Aura.Weapon) Aura.weapon.getValue()).getItem(), (InventoryUtil.Switch) Aura.autoSwitch.getValue());
        Cosmos.INSTANCE.getTickManager().setClientTicks(((Double) Aura.timer.getValue()).doubleValue());
        if (((Boolean) Aura.teleport.getValue()).booleanValue()) {
            TeleportUtil.teleportPlayer(Aura.auraTarget.posX, Aura.auraTarget.posY, Aura.auraTarget.posZ);
        }

        if (!((Rotation.Rotate) Aura.rotate.getValue()).equals(Rotation.Rotate.NONE)) {
            float[] sprint = ((Boolean) Aura.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter(Aura.auraTarget) : AngleUtil.calculateAngles(Aura.auraTarget);

            Aura.auraRotation = new Rotation(((Boolean) Aura.rotateRandom.getValue()).booleanValue() ? sprint[0] + (float) ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : sprint[0], ((Boolean) Aura.rotateRandom.getValue()).booleanValue() ? sprint[1] + (float) ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : sprint[1], (Rotation.Rotate) Aura.rotate.getValue());
            if (!Float.isNaN(Aura.auraRotation.getYaw()) && !Float.isNaN(Aura.auraRotation.getPitch())) {
                Aura.auraRotation.updateModelRotations();
            }
        }

        if (((Boolean) Aura.weaponBlock.getValue()).booleanValue() && InventoryUtil.isHolding(Items.SHIELD)) {
            Aura.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Aura.mc.player.getHorizontalFacing()));
        }

        boolean flag = Aura.mc.player.isSprinting();

        if (((Boolean) Aura.stopSprint.getValue()).booleanValue()) {
            Aura.mc.player.connection.sendPacket(new CPacketEntityAction(Aura.mc.player, net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SPRINTING));
            Aura.mc.player.setSprinting(false);
        }

        if (this.handleDelay()) {
            for (int i = 0; (double) i < ((Double) Aura.iterations.getValue()).doubleValue(); ++i) {
                PlayerUtil.attackEntity(Aura.auraTarget, ((Boolean) Aura.packet.getValue()).booleanValue(), (PlayerUtil.Hand) Aura.swing.getValue(), ((Double) Aura.variation.getValue()).doubleValue());
            }
        }

        if (((Boolean) Aura.stopSprint.getValue()).booleanValue()) {
            if (flag) {
                Aura.mc.player.connection.sendPacket(new CPacketEntityAction(Aura.mc.player, net.minecraft.network.play.client.CPacketEntityAction.Action.START_SPRINTING));
            }

            Aura.mc.player.setSprinting(flag);
        }

        if (Criticals.INSTANCE.isEnabled() && ((Aura.Timing) Aura.timing.getValue()).equals(Aura.Timing.SEQUENTIAL)) {
            Criticals.INSTANCE.handleCriticals(Aura.auraTarget);
            Criticals.INSTANCE.handleFallback(Aura.auraTarget);
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPacketSend(PacketEvent.PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacket() instanceof CPacketPlayer && !Float.isNaN(Aura.auraRotation.getYaw()) && !Float.isNaN(Aura.auraRotation.getPitch()) && ((Rotation.Rotate) Aura.rotate.getValue()).equals(Rotation.Rotate.PACKET)) {
            if (Math.abs(Aura.auraRotation.getYaw() - Aura.mc.player.rotationYaw) >= 20.0F || Math.abs(Aura.auraRotation.getPitch() - Aura.mc.player.rotationPitch) >= 20.0F) {
                for (float step = ((Float) Aura.rotateStep.getValue()).floatValue() - 1.0F; step > 0.0F; --step) {
                    Aura.mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPacketPlayer.Rotation(Aura.auraRotation.getYaw() / step + 1.0F, Aura.auraRotation.getPitch() / step + 1.0F, Aura.mc.player.onGround));
                }
            }

            ((ICPacketPlayer) packetSendEvent.getPacket()).setYaw(Aura.auraRotation.getYaw());
            ((ICPacketPlayer) packetSendEvent.getPacket()).setPitch(Aura.auraRotation.getPitch());
        }

    }

    public boolean handleDelay() {
        if (((Aura.Timing) Aura.timing.getValue()).equals(Aura.Timing.COOLDOWN) || ((Aura.Timing) Aura.timing.getValue()).equals(Aura.Timing.SEQUENTIAL)) {
            switch ((Aura.Delay) Aura.delayMode.getValue()) {
            case TPS:
                return (double) Aura.mc.player.getCooledAttackStrength(((TickManager.TPS) Aura.delayTPS.getValue()).equals(TickManager.TPS.NONE) ? 0.0F : 20.0F - Cosmos.INSTANCE.getTickManager().getTPS((TickManager.TPS) Aura.delayTPS.getValue())) >= ((Double) Aura.delayFactor.getValue()).doubleValue();

            case SWING:
                return (double) Aura.mc.player.getCooledAttackStrength(0.0F) >= ((Double) Aura.delayFactor.getValue()).doubleValue();

            case CUSTOM:
                if (Aura.auraTimer.passed((long) ((Double) Aura.delay.getValue()).doubleValue(), Timer.Format.SYSTEM)) {
                    Aura.auraTimer.reset();
                    return true;
                }
                break;

            case TICK:
                return Aura.auraTimer.passed((long) ((Double) Aura.delayTicks.getValue()).doubleValue(), Timer.Format.TICKS);
            }
        }

        return true;
    }

    public boolean handlePause() {
        return ((Boolean) Aura.pause.getValue()).booleanValue() ? (Cosmos.INSTANCE.getSwitchManager().switchAttackReady((long) ((Double) Aura.delaySwitch.getValue()).doubleValue()) ? false : ((InventoryUtil.isHolding(((Aura.Weapon) Aura.weapon.getValue()).getItem()) || !((Boolean) Aura.weaponOnly.getValue()).booleanValue()) && (InventoryUtil.isHolding32k() || !((Boolean) Aura.weaponThirtyTwoK.getValue()).booleanValue()) ? ((!PlayerUtil.isEating() || !((Boolean) Aura.pauseEating.getValue()).booleanValue()) && (!PlayerUtil.isMining() || !((Boolean) Aura.pauseMining.getValue()).booleanValue()) && (!PlayerUtil.isMending() || !((Boolean) Aura.pauseMending.getValue()).booleanValue()) ? (PlayerUtil.getHealth() <= ((Double) Aura.pauseHealth.getValue()).doubleValue() ? false : ((double) AngleUtil.calculateAngleDifference(Aura.mc.player.rotationYaw, AngleUtil.calculateAngles(Aura.auraTarget)[0]) > ((Double) Aura.fov.getValue()).doubleValue() ? false : RaytraceUtil.raytraceEntity(Aura.auraTarget, (double) Aura.auraTarget.getEyeHeight()) || !((Boolean) Aura.raytrace.getValue()).booleanValue())) : false) : false)) : true;
    }

    private static Boolean lambda$static$4() {
        return Boolean.valueOf(((Aura.Delay) Aura.delayMode.getValue()).equals(Aura.Delay.TPS));
    }

    private static Boolean lambda$static$3() {
        return Boolean.valueOf(((Aura.Delay) Aura.delayMode.getValue()).equals(Aura.Delay.TICK));
    }

    private static Boolean lambda$static$2() {
        return Boolean.valueOf(((Aura.Delay) Aura.delayMode.getValue()).equals(Aura.Delay.CUSTOM));
    }

    private static Boolean lambda$static$1() {
        return Boolean.valueOf(((Aura.Delay) Aura.delayMode.getValue()).equals(Aura.Delay.SWING));
    }

    private static String lambda$new$0() {
        return Setting.formatEnum((Enum) Aura.target.getValue());
    }

    public static enum Weapon {

        SWORD(Items.DIAMOND_SWORD), AXE(Items.DIAMOND_AXE), PICKAXE(Items.DIAMOND_PICKAXE);

        private final Item item;

        private Weapon(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return this.item;
        }
    }

    public static enum Timing {

        SEQUENTIAL, COOLDOWN, NONE;
    }

    public static enum Delay {

        SWING, CUSTOM, TICK, TPS;
    }
}
