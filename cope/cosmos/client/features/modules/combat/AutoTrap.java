package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketPlayer;
import cope.cosmos.util.combat.TargetUtil;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.player.Rotation;
import cope.cosmos.util.world.AngleUtil;
import cope.cosmos.util.world.BlockUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTrap extends Module {

    public static Setting autoSwitch = new Setting("Switch", "Mode to switch to blocks", InventoryUtil.Switch.NORMAL);
    public static Setting blocks = new Setting("Blocks", "Allowed block placements per tick", Double.valueOf(0.0D), Double.valueOf(4.0D), Double.valueOf(10.0D), 0);
    public static Setting swing = new Setting("Swing", "Hand to swing when placing blocks", PlayerUtil.Hand.MAINHAND);
    public static Setting packet = new Setting("Packet", "Place with packets", Boolean.valueOf(false));
    public static Setting confirm = new Setting("Confirm", "Confirm the placement", Boolean.valueOf(false));
    public static Setting rotate = new Setting("Rotation", "Mode for attack rotations", Rotation.Rotate.NONE);
    public static Setting rotateCenter = (new Setting("Center", "Center rotations on target", Boolean.valueOf(false))).setParent(AutoTrap.rotate);
    public static Setting rotateRandom = (new Setting("Random", "Randomize rotations to simulate real rotations", Boolean.valueOf(false))).setParent(AutoTrap.rotate);
    public static Setting target = new Setting("Target", "Priority for searching target", TargetUtil.Target.CLOSEST);
    public static Setting targetRange = (new Setting("Range", "Range to trap players", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(10.0D), 0)).setParent(AutoTrap.target);
    int previousSlot = -1;
    int trapPlaced = 0;
    EntityPlayer trapTarget = null;
    Rotation trapRotation;

    public AutoTrap() {
        super("AutoTrap", Category.COMBAT, "Traps enemies in obsidian");
        this.trapRotation = new Rotation(Float.NaN, Float.NaN, (Rotation.Rotate) AutoTrap.rotate.getValue());
    }

    public void onUpdate() {
        this.trapTarget = TargetUtil.getTargetPlayer(((Double) AutoTrap.targetRange.getValue()).doubleValue(), (TargetUtil.Target) AutoTrap.target.getValue());
        if (this.trapTarget != null) {
            this.trapPlaced = 0;
            this.autoTrap(this.mapTrapPositions());
        }

    }

    public void autoTrap(Iterator trapPositions) {
        this.previousSlot = AutoTrap.mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), (InventoryUtil.Switch) AutoTrap.autoSwitch.getValue());

        while (trapPositions.hasNext()) {
            BlockPos trapPosition = new BlockPos(this.trapTarget.getPositionVector().add((Vec3d) trapPositions.next()));

            if (Objects.equals(BlockUtil.getBlockResistance(trapPosition), BlockUtil.BlockResistance.BLANK) && (double) this.trapPlaced <= ((Double) AutoTrap.blocks.getValue()).doubleValue()) {
                if (!((Rotation.Rotate) AutoTrap.rotate.getValue()).equals(Rotation.Rotate.NONE)) {
                    float[] trapAngles = ((Boolean) AutoTrap.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter(trapPosition) : AngleUtil.calculateAngles(trapPosition);

                    this.trapRotation = new Rotation((float) ((double) trapAngles[0] + (((Boolean) AutoTrap.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (float) ((double) trapAngles[1] + (((Boolean) AutoTrap.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (Rotation.Rotate) AutoTrap.rotate.getValue());
                    if (!Float.isNaN(this.trapRotation.getYaw()) && !Float.isNaN(this.trapRotation.getPitch())) {
                        this.trapRotation.updateModelRotations();
                    }
                }

                BlockUtil.placeBlock(trapPosition, ((Boolean) AutoTrap.packet.getValue()).booleanValue(), ((Boolean) AutoTrap.confirm.getValue()).booleanValue());
                PlayerUtil.swingArm((PlayerUtil.Hand) AutoTrap.swing.getValue());
                ++this.trapPlaced;
            }
        }

        InventoryUtil.switchToSlot(this.previousSlot, InventoryUtil.Switch.NORMAL);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer && !Float.isNaN(this.trapRotation.getYaw()) && !Float.isNaN(this.trapRotation.getPitch())) {
            ((ICPacketPlayer) event.getPacket()).setYaw(this.trapRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(this.trapRotation.getPitch());
        }

    }

    public Iterator mapTrapPositions() {
        if (this.trapTarget != null && this.trapTarget.onGround) {
            boolean middleX = Math.abs((double) Math.round(this.trapTarget.posX) - this.trapTarget.posX) <= 0.3D;
            boolean middleZ = Math.abs((double) Math.round(this.trapTarget.posZ) - this.trapTarget.posZ) <= 0.3D;

            if ((!middleX || !middleZ) && !middleX) {
                if (!middleZ) {
                    return Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, 0.0D)}).iterator();
                }

                if ((double) Math.round(this.trapTarget.posX) - this.trapTarget.posX < 0.0D) {
                    return Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, 0.0D)}).iterator();
                }

                if ((double) Math.round(this.trapTarget.posX) - this.trapTarget.posX > 0.0D) {
                    return Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(1.0D, -1.0D, -1.0D), new Vec3d(1.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, -2.0D), new Vec3d(1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(0.0D, 1.0D, -2.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 2.0D, -2.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, 0.0D)}).iterator();
                }
            }
        }

        return null;
    }
}
