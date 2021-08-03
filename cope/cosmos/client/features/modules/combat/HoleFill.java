package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketPlayer;
import cope.cosmos.util.combat.EnemyUtil;
import cope.cosmos.util.combat.TargetUtil;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.player.Rotation;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.AngleUtil;
import cope.cosmos.util.world.BlockUtil;
import cope.cosmos.util.world.HoleUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleFill extends Module {

    public static HoleFill INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for the filler", HoleFill.Filler.TARGETED);
    public static Setting block = new Setting("Block", "Block to use for filling", HoleFill.Block.OBSIDIAN);
    public static Setting completion = new Setting("Completion", "When to consider the filling complete", HoleFill.Completion.COMPLETION);
    public static Setting range = new Setting("Range", "Range to scan for holes", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(15.0D), 1);
    public static Setting threshold = new Setting(get<invokedynamic>(), "Threshold", "Target\'s distance from hole for it to be considered fill-able", Double.valueOf(0.0D), Double.valueOf(3.0D), Double.valueOf(15.0D), 1);
    public static Setting swing = new Setting("Swing", "Hand to swing when placing", PlayerUtil.Hand.MAINHAND);
    public static Setting autoSwitch = new Setting("Switch", "Mode for switching to block", InventoryUtil.Switch.NORMAL);
    public static Setting doubles = new Setting("Doubles", "Fills in double holes", Boolean.valueOf(true));
    public static Setting packet = new Setting("Packet", "Place with packets", Boolean.valueOf(true));
    public static Setting confirm = new Setting("Confirm", "Confirm the placement", Boolean.valueOf(false));
    public static Setting rotate = new Setting("Rotation", "Mode for placement rotations", Rotation.Rotate.NONE);
    public static Setting rotateCenter = (new Setting("Center", "Center rotations on target", Boolean.valueOf(false))).setParent(HoleFill.rotate);
    public static Setting rotateRandom = (new Setting("Random", "Randomize rotations to simulate real rotations", Boolean.valueOf(false))).setParent(HoleFill.rotate);
    public static Setting target = new Setting("Target", "Priority for searching target", TargetUtil.Target.CLOSEST);
    public static Setting targetRange = (new Setting("Range", "Range to consider a player a target", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(15.0D), 0)).setParent(HoleFill.target);
    public static Setting render = new Setting("Render", "Render a visual of the filling process", Boolean.valueOf(true));
    public static Setting renderMode = (new Setting("Mode", "Style of the visual", RenderBuilder.Box.FILL)).setParent(HoleFill.render);
    public static Setting renderColor = (new Setting("Color", "Color for the visual", new Color(250, 0, 250, 50))).setParent(HoleFill.render);
    int previousSlot;
    EntityPlayer fillTarget;
    public static BlockPos fillPosition = BlockPos.ORIGIN;
    Rotation fillRotation;

    public HoleFill() {
        super("HoleFill", Category.COMBAT, "Fills in nearby holes");
        this.fillRotation = new Rotation(Float.NaN, Float.NaN, (Rotation.Rotate) HoleFill.rotate.getValue());
        HoleFill.INSTANCE = this;
    }

    public void onThread() {
        HoleFill.fillPosition = BlockPos.ORIGIN;
        this.fillTarget = (EntityPlayer) TargetUtil.getTargetEntity(((Double) HoleFill.targetRange.getValue()).doubleValue(), TargetUtil.Target.CLOSEST, true, false, false, false);
        if (this.fillTarget != null && !EnemyUtil.isDead(this.fillTarget)) {
            TreeMap fillMap = new TreeMap();
            Iterator potentialHoles = null;

            switch ((HoleFill.Filler) HoleFill.mode.getValue()) {
            case TARGETED:
                potentialHoles = BlockUtil.getNearbyBlocks(this.fillTarget, ((Double) HoleFill.threshold.getValue()).doubleValue(), false);
                break;

            case ALL:
                potentialHoles = BlockUtil.getNearbyBlocks(HoleFill.mc.player, ((Double) HoleFill.range.getValue()).doubleValue(), false);
            }

            Object distanceTarget = ((HoleFill.Filler) HoleFill.mode.getValue()).equals(HoleFill.Filler.TARGETED) ? this.fillTarget : HoleFill.mc.player;

            while (potentialHoles.hasNext()) {
                BlockPos calculatedHole = (BlockPos) potentialHoles.next();

                if (HoleFill.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(calculatedHole)).isEmpty()) {
                    if (HoleUtil.isBedRockHole(calculatedHole) || HoleUtil.isObsidianHole(calculatedHole)) {
                        fillMap.put(Double.valueOf(((EntityPlayer) distanceTarget).getDistanceSq(calculatedHole)), calculatedHole);
                    }

                    if (((Boolean) HoleFill.doubles.getValue()).booleanValue() && (HoleUtil.isDoubleBedrockHoleZ(calculatedHole) || HoleUtil.isDoubleBedrockHoleX(calculatedHole) || HoleUtil.isDoubleObsidianHoleZ(calculatedHole) || HoleUtil.isDoubleObsidianHoleX(calculatedHole))) {
                        fillMap.put(Double.valueOf(((EntityPlayer) distanceTarget).getDistanceSq(calculatedHole)), calculatedHole);
                    }
                }
            }

            switch ((HoleFill.Completion) HoleFill.completion.getValue()) {
            case COMPLETION:
                if (fillMap.isEmpty()) {
                    this.disable();
                }
                break;

            case TARGET:
                if (this.fillTarget == null || EnemyUtil.isDead(this.fillTarget)) {
                    this.disable();
                }

            case PERSISTENT:
            }

            HoleFill.fillPosition = (BlockPos) fillMap.firstEntry().getValue();
        }
    }

    public void onUpdate() {
        if (HoleFill.fillPosition.equals(BlockPos.ORIGIN)) {
            this.fillTarget = null;
            HoleFill.fillPosition = null;
        } else {
            this.previousSlot = HoleFill.mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(((HoleFill.Block) HoleFill.block.getValue()).getItem(), (InventoryUtil.Switch) HoleFill.autoSwitch.getValue());
            if (HoleFill.fillPosition != null && !((Rotation.Rotate) HoleFill.rotate.getValue()).equals(Rotation.Rotate.NONE)) {
                float[] fillAngles = ((Boolean) HoleFill.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter(HoleFill.fillPosition) : AngleUtil.calculateAngles(HoleFill.fillPosition);

                this.fillRotation = new Rotation((float) ((double) fillAngles[0] + (((Boolean) HoleFill.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (float) ((double) fillAngles[1] + (((Boolean) HoleFill.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (Rotation.Rotate) HoleFill.rotate.getValue());
                if (!Float.isNaN(this.fillRotation.getYaw()) && !Float.isNaN(this.fillRotation.getPitch())) {
                    this.fillRotation.updateModelRotations();
                }
            }

            if (HoleFill.fillPosition != null && HoleFill.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(HoleFill.fillPosition)).isEmpty()) {
                if (HoleFill.fillPosition != BlockPos.ORIGIN && InventoryUtil.isHolding(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                    BlockUtil.placeBlock(HoleFill.fillPosition, ((Boolean) HoleFill.packet.getValue()).booleanValue(), ((Boolean) HoleFill.confirm.getValue()).booleanValue());
                    PlayerUtil.swingArm((PlayerUtil.Hand) HoleFill.swing.getValue());
                }

                InventoryUtil.switchToSlot(this.previousSlot, InventoryUtil.Switch.NORMAL);
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer && !Float.isNaN(this.fillRotation.getYaw()) && !Float.isNaN(this.fillRotation.getPitch()) && ((Rotation.Rotate) HoleFill.rotate.getValue()).equals(Rotation.Rotate.PACKET)) {
            ((ICPacketPlayer) event.getPacket()).setYaw(this.fillRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(this.fillRotation.getPitch());
        }

    }

    public void onRender3d() {
        if (this.nullCheck() && HoleFill.fillPosition != BlockPos.ORIGIN && ((Boolean) HoleFill.render.getValue()).booleanValue()) {
            RenderUtil.drawBox((new RenderBuilder()).position(new BlockPos(HoleFill.fillPosition)).color((Color) HoleFill.renderColor.getValue()).setup().line(1.5F).cull(((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleFill.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(true).blend().texture());
        }

    }

    private static Boolean lambda$static$0() {
        return Boolean.valueOf(((HoleFill.Filler) HoleFill.mode.getValue()).equals(HoleFill.Filler.TARGETED));
    }

    private static enum Block {

        OBSIDIAN(Item.getItemFromBlock(Blocks.OBSIDIAN)), ENDER_CHEST(Item.getItemFromBlock(Blocks.ENDER_CHEST)), PRESSURE_PLATE(Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE)), WEB(Item.getItemFromBlock(Blocks.WEB));

        private final Item item;

        private Block(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return this.item;
        }
    }

    public static enum Completion {

        COMPLETION, TARGET, PERSISTENT;
    }

    public static enum Filler {

        ALL, TARGETED;
    }
}
